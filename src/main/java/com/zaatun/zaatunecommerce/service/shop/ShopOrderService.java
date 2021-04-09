package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderProductRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.*;
import com.zaatun.zaatunecommerce.service.UtilService;
import lombok.AllArgsConstructor;
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
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;
    private final CouponRepository couponRepository;
    private final UtilService utilService;

    public ResponseEntity<ApiResponse<String >> placeOrder(OrderPlaceRequest orderPlaceRequest, String token) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);
        String orderId = "ZTN-O-" + basicTableInfo.getId().substring(0, 8).toUpperCase() + "-" +
                java.time.LocalTime.now().toString().substring(9);


        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(jwtProvider.getUserNameFromJwt(token));
        if(profileModelOptional.isPresent()){
            ProfileModel profileModel = profileModelOptional.get();

            if(profileModel.getDeliveryAddresses().contains(orderPlaceRequest.getDeliveryAddress())){
                Integer shippingTotal = calculateShippingCharge(orderPlaceRequest.getDeliveryAddress());
                Integer adminDiscount = 0;
                List<OrderProductModel> orderProductModelList = fromProductIdListToProductList(orderPlaceRequest.getProducts(), token);
                Integer productPriceTotal = calculateProductPriceTotal(orderProductModelList);
                CouponModel couponModel = calculateCouponDiscount(orderPlaceRequest.getCouponCode(), productPriceTotal);
                assert couponModel != null;
                Integer subTotal = calculateTotal(productPriceTotal, adminDiscount, couponModel.getCouponAmount());

                OrderModel orderModel = OrderModel.builder()
                        .id(basicTableInfo.getId())
                        .orderId(orderId)
                        .createBy(basicTableInfo.getCreateBy())
                        .createdOn(basicTableInfo.getCreationTime())
                        .userName(jwtProvider.getUserNameFromJwt(token))
                        .orderItems(orderProductModelList)
                        .orderStatus("Pending")
                        .deliveryAddress(orderPlaceRequest.getDeliveryAddress())
                        .productPriceTotal(productPriceTotal)
                        .paymentMethod(orderPlaceRequest.getPaymentMethod())
                        .paymentStatus("Unpaid")
                        .shippingCharge(shippingTotal)
                        .couponDiscount(couponModel.getCouponAmount())
                        .subTotal(subTotal)
                        .totalAmount(subTotal+shippingTotal)
                        .couponModel(couponModel)
                        .build();

                orderRepository.save(orderModel);

                return new ResponseEntity<>(new ApiResponse<>(201, "Order Placed Successful", orderId), HttpStatus.CREATED);
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is not matched with Profile");
            }

        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile Not Found");
        }


    }

    private Integer calculateTotal(Integer productPriceTotal, Integer adminDiscount, Integer couponDiscount) {
        Integer total = productPriceTotal - adminDiscount;
        total-= couponDiscount;

        return total;
    }

    private CouponModel calculateCouponDiscount(String couponCode, Integer productPriceTotal) {
        Optional<CouponModel> couponModelOptional = couponRepository.findByCouponCode(couponCode);

        if(couponModelOptional.isPresent()){
            CouponModel couponModel = couponModelOptional.get();
            long currentTime = System.currentTimeMillis();

            if(currentTime >= couponModel.getValidFrom() && currentTime <= couponModel.getValidTill()){
                if (productPriceTotal >= couponModel.getMinimumBuy()){
                    return couponModel;
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to buy minimum amount "+
                            couponModel.getMinimumBuy()+ "Taka to avail this coupon.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon Time expired");
            }

        }
        return null;
    }

    private Integer calculateShippingCharge(DeliveryAddressModel deliveryAddress) {

        return 100;
    }

    private Integer calculateProductPriceTotal(List<OrderProductModel> orderProductModelList) {
        Integer productPriceTotal = 0;

        for (OrderProductModel orderProductModel: orderProductModelList){
            Integer discountPrice = orderProductModel.getProduct().getDiscountPrice();
            Integer regularPrice = orderProductModel.getProduct().getRegularPrice();
            if(discountPrice != null && discountPrice != 0){
                productPriceTotal += discountPrice;
            }
            else {
                productPriceTotal += regularPrice;
            }
        }

        return productPriceTotal;
    }

    private List<OrderProductModel> fromProductIdListToProductList(List<OrderProductRequest> orderProductRequestList, String token) {
        List<OrderProductModel> orderProductModelList = new ArrayList<>();

        for (OrderProductRequest orderProductRequest : orderProductRequestList) {
            OrderProductModel orderProductModel = new OrderProductModel();
            Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(orderProductRequest.getProductSlug());

            if (productModelOptional.isPresent()) {
                ProductModel productModel = productModelOptional.get();

                List<ProductVariantModel> productVariantModelList = productModel.getVariants();

                if (productVariantModelList.contains(orderProductRequest.getProductVariant())) {

                    boolean checkAllStock = false;
                    for (ProductVariantModel productVariantModel : productVariantModelList) {
                        if (productVariantModel.getId().equals(orderProductRequest.getProductVariant().getId())) {
                            Integer productQuantityInDb = productVariantModel.getQuantity();
                            Integer productQuantityInOrder = orderProductRequest.getQuantity();
                            if (productQuantityInDb >= productQuantityInOrder) {
                                productQuantityInDb -= productQuantityInOrder;
                                productVariantModel.setQuantity(productQuantityInDb);
                                productModel.setTotalSold(productModel.getTotalSold() + productQuantityInOrder);
                                if (productQuantityInDb == 0) {
                                    productVariantModel.setInStock(false);
                                }
                            } else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order Quantity of "
                                        + productModel.getProductName() + " is not available");
                            }
                        }

                        if (productVariantModel.getQuantity() > 0) {
                            checkAllStock = true;
                        }
                    }
                    productModel.setInStock(checkAllStock);
                    List<String> buyers = productModel.getBuyersId();
                    buyers.add(jwtProvider.getUserNameFromJwt(token));
                    productModel.setBuyersId(buyers);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Variant is not matched with product");
                }
                orderProductModel.setProduct(productModel);
                orderProductModel.setProductVariant(orderProductRequest.getProductVariant());
                orderProductModel.setQuantity(orderProductRequest.getQuantity());

                orderProductModelList.add(orderProductModel);
                productRepository.save(productModel);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Product not found with the slug " + orderProductRequest.getProductSlug());
            }
        }
        return orderProductModelList;
    }
}
