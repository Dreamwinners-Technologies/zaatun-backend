package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProcessHistory;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.*;
import com.zaatun.zaatunecommerce.service.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ShopOrderService {
    private final JwtProvider jwtProvider;
    private final OrderRepository orderRepository;
    private final ProfileRepository profileRepository;
    private final UtilService utilService;
    private final ShopOrderServiceExtended shopOrderServiceExtended;
    private final ShopProductHelperService shopProductHelperService;

    public ResponseEntity<ApiResponse<String>> placeOrder(OrderPlaceRequest orderPlaceRequest, String token) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);
        String orderId = "ZTN-O-" + basicTableInfo.getId().substring(0, 8).toUpperCase() + "-" +
                java.time.LocalTime.now().toString().substring(9, 12);


        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(jwtProvider.getUserNameFromJwt(token));
        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            Optional<DeliveryAddressModel> deliveryAddressModelOptional =
                    shopOrderServiceExtended.getDeliveryAddress(profileModel, orderPlaceRequest.getDeliveryAddressId());

            if (deliveryAddressModelOptional.isPresent()) {
                DeliveryAddressModel deliveryAddressModel = deliveryAddressModelOptional.get();

                if (profileModel.getDeliveryAddresses().contains(deliveryAddressModel)) {
                    Integer shippingTotal = shopOrderServiceExtended.calculateShippingCharge(deliveryAddressModel);
                    Integer adminDiscount = 0;

                    Integer productPriceTotal = shopOrderServiceExtended.calculateTotalProductPrice(orderPlaceRequest.getProducts());

                    Optional<CouponModel> couponModelOptional =
                            shopOrderServiceExtended.calculateCouponDiscount(orderPlaceRequest.getCouponCode(), productPriceTotal);

                    List<OrderProductModel> orderProductModelList =
                            shopOrderServiceExtended.fromProductIdListToProductList(orderPlaceRequest.getProducts(), token);
                    List<OrderProcessHistoryModel> orderProcessHistoryModels = new ArrayList<>();
                    orderProcessHistoryModels.add(new OrderProcessHistoryModel(1L, null, null,
                            "Pending", "Your Order is Pending - System", "Status Pending"));

                    OrderModel orderModel = OrderModel.builder()
                            .id(basicTableInfo.getId())
                            .orderId(orderId)
                            .createBy(basicTableInfo.getCreateBy())
                            .createdOn(basicTableInfo.getCreationTime())
                            .userName(jwtProvider.getUserNameFromJwt(token))
                            .orderItems(orderProductModelList)
                            .orderStatus("Pending")
                            .deliveryAddress(deliveryAddressModel)
                            .productPriceTotal(productPriceTotal)
                            .paymentMethod(orderPlaceRequest.getPaymentMethod())
                            .paymentStatus("Unpaid")
                            .shippingCharge(shippingTotal)
                            .orderProcessHistory(orderProcessHistoryModels)
                            .build();

                    Integer subTotal;
                    if (couponModelOptional.isPresent()) {
                        CouponModel couponModel = couponModelOptional.get();
                        subTotal = shopOrderServiceExtended.calculateTotal(productPriceTotal, adminDiscount, couponModel.getCouponAmount());
                        orderModel.setCouponModel(couponModel);
                        orderModel.setCouponDiscount(couponModel.getCouponAmount());
                    } else {
                        subTotal = shopOrderServiceExtended.calculateTotal(productPriceTotal, adminDiscount, 0);
                    }

                    orderModel.setSubTotal(subTotal);
                    orderModel.setTotalAmount(subTotal + shippingTotal);
                    orderRepository.save(orderModel);

                    profileModel.setTotalOrders(profileModel.getTotalOrders() + 1);
                    profileModel.setTotalOrderAmounts(profileModel.getTotalOrderAmounts() + subTotal + shippingTotal);
                    profileRepository.save(profileModel);

                    return new ResponseEntity<>(new ApiResponse<>(201, "Order Placed Successful", orderId), HttpStatus.CREATED);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is not matched with Profile");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address Not Found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile Not Found");
        }


    }

    public ResponseEntity getOrderInfos(String token, Integer pageNo, Integer pageSize) {
        String username = jwtProvider.getUserNameFromJwt(token);

        Sort sort = Sort.by("createdOn").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<OrderModel> orderModelsPageable = orderRepository.findByUserName(username, pageable);

        List<ShopOrderResponse> shopOrderResponses = new ArrayList<>();

        for (OrderModel orderModel : orderModelsPageable.getContent()) {
            List<OrderProductModel> orderProductModels = orderModel.getOrderItems();
            List<ProductModel> productModels = new ArrayList<>();

            for (OrderProductModel orderProductModel : orderProductModels) {
                List<ProductVariantModel> productVariantModels = new ArrayList<>();
                ProductVariantModel productVariantModel = orderProductModel.getProductVariant();
                productVariantModel.setQuantity(orderProductModel.getQuantity());
                productVariantModels.add(productVariantModel);

                ProductModel productModel = orderProductModel.getProduct();
                productModel.setVariants(productVariantModels);

                productModels.add(productModel);
            }
            List<ShopProductResponse> shopProductResponseList =
                    new ArrayList<>(shopProductHelperService.shopProductResponseFromProductsDb(productModels));

            List<ShopOrderProcessHistory> shopOrderProcessHistories =
                    shopOrderServiceExtended.orderProcessHistoryForShop(orderModel);

            ShopOrderResponse shopOrderResponse = new ShopOrderResponse(orderModel.getOrderId(), orderModel.getInvoiceId(),
                    orderModel.getUserName(), shopProductResponseList, orderModel.getDeliveryAddress(),
                    orderModel.getOrderStatus(), orderModel.getProductPriceTotal(), orderModel.getPaidAmount(),
                    orderModel.getPaymentMethod(), orderModel.getPaymentStatus(), orderModel.getShippingCharge(),
                    orderModel.getAdminDiscount(), orderModel.getAdminDiscountAddedBy(), orderModel.getCouponDiscount(),
                    orderModel.getSubTotal(), orderModel.getTotalAmount(), orderModel.getTransactionId(),
                    orderModel.getCouponModel(), shopOrderProcessHistories
            );
            shopOrderResponses.add(shopOrderResponse);
        }


        PaginationResponse<List<ShopOrderResponse>> paginationResponse =
                new PaginationResponse<>(pageSize, pageNo, orderModelsPageable.getContent().size(), orderModelsPageable.isLast(),
                        orderModelsPageable.getTotalElements(), orderModelsPageable.getTotalPages(), shopOrderResponses);

        return new ResponseEntity<>(new ApiResponse<>(200, "Orders Found", paginationResponse), HttpStatus.OK);
    }


}
