package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderProductRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProcessHistoryResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProductResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderResponse;
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
import java.util.Set;

@AllArgsConstructor
@Service
public class ShopOrderService {
    private final JwtProvider jwtProvider;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;
    private final UtilService utilService;
    private final ShopOrderServiceExtended shopOrderServiceExtended;
    private final ProductVariationRepository productVariationRepository;

    //Order Place Request


    public ResponseEntity<ApiResponse<String>> placeOrder(OrderPlaceRequest orderPlaceRequest, String token) {
        //Getting UserName from JWT Token
        String username = jwtProvider.getUserNameFromJwt(token);

        //Generate BasicTable Infos like: Id, Slug, created by, created on, SKU based on JWT Token
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

        //Setting up Unique and Meaningful Order ID
        //Get first 8 digit of UUID from basic table, then take last 4 values from current times
        String orderId = "ZTN-O-" + basicTableInfo.getId().substring(0, 8).toUpperCase() + "-" +
                java.time.LocalTime.now().toString().substring(9, 12);

        //Getting User Profile from Database using username
        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);

        //Checking if there is a profile with this userName
        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            //Getting Delivery Address using address ID from Database
            Optional<DeliveryAddressModel> deliveryAddressModelOptional =
                    shopOrderServiceExtended.getDeliveryAddress(profileModel, orderPlaceRequest.getDeliveryAddressId());

            //If deliveryAddress found then go to this section
            if (deliveryAddressModelOptional.isPresent()) {
                DeliveryAddressModel deliveryAddressModel = deliveryAddressModelOptional.get();

                //Check if the buyers profile has the deliveryAddress
                if (profileModel.getDeliveryAddresses().contains(deliveryAddressModel)) {

                    //Setting admin discount to 0
                    Integer adminDiscount = 0;

                    //Separate all the product slugs from orderList and
                    List<String> slugs = new ArrayList<>();
                    for (OrderProductRequest orderProductRequest : orderPlaceRequest.getProducts()) {
                        slugs.add(orderProductRequest.getProductSlug());
                    }

                    List<Long> variantIds = new ArrayList<>();
                    for (OrderProductRequest orderProductRequest : orderPlaceRequest.getProducts()) {
                        variantIds.add(orderProductRequest.getVariationId());
                    }

                    //Get Product Info List from the database using product slugs from order information
                    List<ProductVariationModel> productVariationModels = productVariationRepository.findByIdIn(variantIds);

                    //Check if
                    if (productVariationModels.size() != orderPlaceRequest.getProducts().size()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All Products are not found on Database");
                    }

                    for (ProductVariationModel productVariationModel : productVariationModels) {

                        boolean isNotFound = true;
                        for (OrderProductRequest orderProductRequest : orderPlaceRequest.getProducts()) {
                            if (orderProductRequest.getProductSlug().equals(productVariationModel.getProductModel().getProductSlug())) {
                                isNotFound = false;
                                break;
                            }
                        }
                        if (isNotFound) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All Products are not found");
                        }
                    }

                    //Get the total price of products
                    Integer totalProductPrice = shopOrderServiceExtended.getTotalProductPrice(productVariationModels, orderPlaceRequest.getProducts());
                    CouponModel couponModel = null;
                    Integer couponDiscount = 0;

                    //Check if coupon is valid then return the coupon discount amount
                    if (!orderPlaceRequest.getCouponCode().isEmpty()) {
                        couponModel = shopOrderServiceExtended.getCouponDiscount(totalProductPrice, orderPlaceRequest.getCouponCode());
                        couponDiscount = couponModel.getCouponAmount();
                    }

                    //Migrate ProductModel to OrderProductModel and reduce product stock
                    List<OrderProductModel> orderProductModelList = shopOrderServiceExtended.reduceStock(orderPlaceRequest, productVariationModels, username);

                    //Adding initial product status
                    List<OrderProcessHistoryModel> orderProcessHistoryModels =
                            getOrderProcessHistoryModels(username, basicTableInfo);

                    //Saving new Delivery address
                    DeliveryAddressModel deliveryAddress = buildNewDeliveryAddress(deliveryAddressModel);

                    //Getting shipping charge
                    Integer shippingCharge = shopOrderServiceExtended.calculateShippingCharge(deliveryAddress);

                    //Calculating total amount
                    Integer totalAmount = shopOrderServiceExtended.calculateTotal(totalProductPrice, adminDiscount, couponDiscount, shippingCharge);

                    //Setting all order data to OrderModel
                    OrderModel orderModel = OrderModel.builder()
                            .id(basicTableInfo.getId())
                            .orderId(orderId)
                            .createBy(basicTableInfo.getCreateBy())
                            .createdOn(basicTableInfo.getCreationTime())
                            .userName(username)
                            .orderItems(orderProductModelList)
                            .deliveryAddress(deliveryAddress)
                            .orderStatus("Pending")
                            .productPriceTotal(totalProductPrice)
                            .paidAmount(0)
                            .paymentMethod(orderPlaceRequest.getPaymentMethod())
                            .paymentStatus("Unpaid")
                            .shippingCharge(shippingCharge)
                            .couponDiscount(couponDiscount)
                            .adminDiscount(adminDiscount)
                            .subTotal(totalProductPrice)
                            .totalAmount(totalAmount)
                            .couponModel(couponModel)
                            .orderProcessHistory(orderProcessHistoryModels)
                            .isCompleted(false)
                            .orderComment(orderPlaceRequest.getOrderComment())
                            .build();


                    profileModel.setTotalOrders(profileModel.getTotalOrders() + 1);     //Increase total order of user
                    profileModel.setTotalOrderAmounts(profileModel.getTotalOrderAmounts() + totalAmount);       //Increase total amount of user


                    List<ProductModel> productModels = new ArrayList<>();
                    for (ProductVariationModel productVariationModel: productVariationModels){
                        productModels.add(productVariationModel.getProductModel());
                    }

                    //Affiliate Functionality starts
                    shopOrderServiceExtended.affiliateFunctionalities(orderPlaceRequest, profileModel, productModels);


                    orderRepository.save(orderModel);
                    productVariationRepository.saveAll(productVariationModels);
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


    private List<OrderProcessHistoryModel> getOrderProcessHistoryModels(String username, BasicTableInfo basicTableInfo) {
        List<OrderProcessHistoryModel> orderProcessHistoryModels = new ArrayList<>();
        OrderProcessHistoryModel orderProcessHistoryModel = OrderProcessHistoryModel.builder()
                .updateBy("System - User")
                .updatedOn(basicTableInfo.getCreationTime())
                .orderStatus("PENDING")
                .employeeNote("Order Placed by: " + username)
                .customerNote("Your order is in pending phase")
                .build();
        orderProcessHistoryModels.add(orderProcessHistoryModel);
        return orderProcessHistoryModels;
    }

    private DeliveryAddressModel buildNewDeliveryAddress(DeliveryAddressModel deliveryAddressModel) {
        DeliveryAddressModel deliveryAddress = new DeliveryAddressModel();
        deliveryAddress.setFullName(deliveryAddressModel.getFullName());
        deliveryAddress.setAddress(deliveryAddressModel.getAddress());
        deliveryAddress.setPhoneNo(deliveryAddressModel.getPhoneNo());
        deliveryAddress.setArea(deliveryAddressModel.getArea());
        deliveryAddress.setCity(deliveryAddressModel.getCity());
        deliveryAddress.setRegion(deliveryAddress.getRegion());

        return deliveryAddress;
    }

    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopOrderResponse>>>> getOrderInfos(String token, Integer pageNo, Integer pageSize) {
        String username = jwtProvider.getUserNameFromJwt(token);

        Sort sort = Sort.by("createdOn").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<OrderModel> orderModelsPageable = orderRepository.findByUserName(username, pageable);

        List<ShopOrderResponse> shopOrderResponses = new ArrayList<>();

        for (OrderModel orderModel : orderModelsPageable.getContent()) {
            List<OrderProductModel> orderProductModels = orderModel.getOrderItems();

//            List<ShopOrderProductResponse> shopOrderProductResponses = new ArrayList<>();

//            for (OrderProductModel orderProductModel : orderProductModels) {
//                ShopOrderProductResponse productResponse = new ShopOrderProductResponse(orderProductModel.getProductName(),
//                        orderProductModel.getProductSlug(), orderProductModel.getSKU(), orderProductModel.getBrand(),
//                        orderProductModel.getCategoryName(), orderProductModel.getSubCategoryName(),
//                        orderProductModel.getShortDescription(), orderProductModel.getVat(), orderProductModel.getDeliveryInfo(),
//                        orderProductModel.getProductImages(), orderProductModel.getQuantity());
//
//                shopOrderProductResponses.add(productResponse);
//            }

//            List<ShopOrderProductResponse> shopProductResponseList =
//                    new ArrayList<>(shopProductHelperService.shopOrderProductResponseFromProducts(productModels));

            List<ShopOrderProcessHistoryResponse> shopOrderProcessHistories =
                    shopOrderServiceExtended.orderProcessHistoryForShop(orderModel);

            ShopOrderResponse shopOrderResponse = new ShopOrderResponse(orderModel.getOrderId(), orderModel.getInvoiceId(),
                    orderModel.getUserName(), orderModel.getOrderItems(), orderModel.getDeliveryAddress(),
                    orderModel.getOrderStatus(), orderModel.getProductPriceTotal(), orderModel.getPaidAmount(),
                    orderModel.getPaymentMethod(), orderModel.getPaymentStatus(), orderModel.getShippingCharge(),
                    orderModel.getAdminDiscount(), orderModel.getCouponDiscount(), orderModel.getSubTotal(),
                    orderModel.getTotalAmount(), orderModel.getTransactionId(), shopOrderProcessHistories
            );
            shopOrderResponses.add(shopOrderResponse);
        }


        PaginationResponse<List<ShopOrderResponse>> paginationResponse =
                new PaginationResponse<>(pageSize, pageNo, orderModelsPageable.getContent().size(), orderModelsPageable.isLast(),
                        orderModelsPageable.getTotalElements(), orderModelsPageable.getTotalPages(), shopOrderResponses);

        if (orderModelsPageable.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Orders Found", paginationResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Orders Found", paginationResponse), HttpStatus.OK);
        }
    }


}

//    public ResponseEntity<ApiResponse<String>> placeOrder(OrderPlaceRequest orderPlaceRequest, String token) {
//        //Getting UserName from JWT Token
//        String username = jwtProvider.getUserNameFromJwt(token);
//
//        //Generate BasicTable Infos like: Id, Slug, created by, created on, SKU based on JWT Token
//        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);
//
//        //Setting up Unique and Meaningful Order ID
//        //Get first 8 digit of UUID from basic table, then take last 4 values from current times
//        String orderId = "ZTN-O-" + basicTableInfo.getId().substring(0, 8).toUpperCase() + "-" +
//                java.time.LocalTime.now().toString().substring(9, 12);
//
//        //Getting User Profile from Database using username
//        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);
//
//        //Checking if there is a profile with this userName
//        if (profileModelOptional.isPresent()) {
//            ProfileModel profileModel = profileModelOptional.get();
//
//            //Getting Delivery Address using address ID from Database
//            Optional<DeliveryAddressModel> deliveryAddressModelOptional =
//                    shopOrderServiceExtended.getDeliveryAddress(profileModel, orderPlaceRequest.getDeliveryAddressId());
//
//            //If deliveryAddress found then go to this section
//            if (deliveryAddressModelOptional.isPresent()) {
//                DeliveryAddressModel deliveryAddressModel = deliveryAddressModelOptional.get();
//
//                //Check if the buyers profile has the deliveryAddress
//                if (profileModel.getDeliveryAddresses().contains(deliveryAddressModel)) {
//
//                    //Setting admin discount to 0
//                    Integer adminDiscount = 0;
//
//                    //Separate all the product slugs from orderList and
//                    List<String> slugs = new ArrayList<>();
//                    for (OrderProductRequest orderProductRequest : orderPlaceRequest.getProducts()) {
//                        slugs.add(orderProductRequest.getProductSlug());
//                    }
//
//                    //Get Product Info List from the database using product slugs from order information
//                    List<ProductModel> productModels = productRepository.findByProductSlugIn(slugs);
//
//                    //Check if
//                    if (productModels.size() != orderPlaceRequest.getProducts().size()) {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All Products are not found on Database");
//                    }
//
//                    //Get the total price of products
//                    Integer totalProductPrice = shopOrderServiceExtended.getTotalProductPrice(productModels, orderPlaceRequest.getProducts());
//                    CouponModel couponModel = null;
//                    Integer couponDiscount = 0;
//
//                    //Check if coupon is valid then return the coupon discount amount
//                    if (!orderPlaceRequest.getCouponCode().isEmpty()) {
//                        couponModel = shopOrderServiceExtended.getCouponDiscount(totalProductPrice, orderPlaceRequest.getCouponCode());
//                        couponDiscount = couponModel.getCouponAmount();
//                    }
//
//                    //Migrate ProductModel to OrderProductModel and reduce product stock
//                    List<OrderProductModel> orderProductModelList =
//                            shopOrderServiceExtended.fromProductsToOrderProducts(productModels, orderPlaceRequest.getProducts(), username);
//
//                    //Adding initial product status
//                    List<OrderProcessHistoryModel> orderProcessHistoryModels =
//                            getOrderProcessHistoryModels(username, basicTableInfo);
//
//                    //Saving new Delivery address
//                    DeliveryAddressModel deliveryAddress = buildNewDeliveryAddress(deliveryAddressModel);
//
//                    //Getting shipping charge
//                    Integer shippingCharge = shopOrderServiceExtended.calculateShippingCharge(deliveryAddress);
//
//                    //Calculating total amount
//                    Integer totalAmount = shopOrderServiceExtended.calculateTotal(totalProductPrice, adminDiscount, couponDiscount, shippingCharge);
//
//                    //Setting all order data to OrderModel
//                    OrderModel orderModel = OrderModel.builder()
//                            .id(basicTableInfo.getId())
//                            .orderId(orderId)
//                            .createBy(basicTableInfo.getCreateBy())
//                            .createdOn(basicTableInfo.getCreationTime())
//                            .userName(username)
//                            .orderItems(orderProductModelList)
//                            .deliveryAddress(deliveryAddress)
//                            .orderStatus("Pending")
//                            .productPriceTotal(totalProductPrice)
//                            .paidAmount(0)
//                            .paymentMethod(orderPlaceRequest.getPaymentMethod())
//                            .paymentStatus("Unpaid")
//                            .shippingCharge(shippingCharge)
//                            .couponDiscount(couponDiscount)
//                            .adminDiscount(adminDiscount)
//                            .subTotal(totalProductPrice)
//                            .totalAmount(totalAmount)
//                            .couponModel(couponModel)
//                            .orderProcessHistory(orderProcessHistoryModels)
//                            .isCompleted(false)
//                            .orderComment(orderPlaceRequest.getOrderComment())
//                            .build();
//
//
//                    profileModel.setTotalOrders(profileModel.getTotalOrders() + 1);     //Increase total order of user
//                    profileModel.setTotalOrderAmounts(profileModel.getTotalOrderAmounts() + totalAmount);       //Increase total amount of user
//
//                    //Affiliate Functionality starts
//                    shopOrderServiceExtended.affiliateFunctionalities(orderPlaceRequest, profileModel, productModels);
//
//
//                    orderRepository.save(orderModel);
//                    productRepository.saveAll(productModels);
//                    profileRepository.save(profileModel);
//
//                    return new ResponseEntity<>(new ApiResponse<>(201, "Order Placed Successful", orderId), HttpStatus.CREATED);
//                } else {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is not matched with Profile");
//                }
//            } else {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address Not Found");
//            }
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile Not Found");
//        }
//
//    }