package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderProductRequest;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProcessHistoryResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@AllArgsConstructor
@Service
public class ShopOrderServiceExtended {

    private final JwtProvider jwtProvider;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final AffiliateUserTrackerRepository affiliateUserTrackerRepository;
    private final AffiliateUserRepository affiliateUserRepository;
    private final ProductVariationRepository productVariationRepository;

    Optional<DeliveryAddressModel> getDeliveryAddress(ProfileModel profileModel, Long deliveryAddressId) {
        List<DeliveryAddressModel> deliveryAddressModelList = profileModel.getDeliveryAddresses();
        long deliveryAddressIdRequest = deliveryAddressId;
        for (DeliveryAddressModel deliveryAddressModel : deliveryAddressModelList) {
            long deliveryAddressIdDb = deliveryAddressModel.getAddressId();
            if (deliveryAddressIdDb == deliveryAddressIdRequest) {
                return Optional.of(deliveryAddressModel);
            }
        }
        return Optional.empty();
    }

    Integer calculateTotal(Integer productPriceTotal, Integer adminDiscount, Integer couponDiscount, Integer shippingCharge) {
        Integer total = productPriceTotal - adminDiscount;
        total -= couponDiscount;
        total += shippingCharge;

        return total;
    }

    Integer calculateShippingCharge(DeliveryAddressModel deliveryAddress) {

        return 100;
    }

    List<ShopOrderProcessHistoryResponse> orderProcessHistoryForShop(OrderModel orderModel) {
        List<ShopOrderProcessHistoryResponse> shopOrderProcessHistories = new ArrayList<>();
        for (OrderProcessHistoryModel orderProcessHistory : orderModel.getOrderProcessHistory()) {
            ShopOrderProcessHistoryResponse shopOrderProcessHistoryResponse =
                    new ShopOrderProcessHistoryResponse(orderProcessHistory.getUpdateBy(), orderProcessHistory.getUpdatedOn(),
                            orderProcessHistory.getOrderStatus(), orderProcessHistory.getCustomerNote());

            shopOrderProcessHistories.add(shopOrderProcessHistoryResponse);
        }
        shopOrderProcessHistories.sort(new Comparator<ShopOrderProcessHistoryResponse>() {
            @Override
            public int compare(ShopOrderProcessHistoryResponse o1, ShopOrderProcessHistoryResponse o2) {
                return o2.getUpdatedOn().compareTo(o1.getUpdatedOn());
            }
        });
        return shopOrderProcessHistories;
    }

    //calculating the total price of products
    public Integer getTotalProductPrice(List<ProductVariationModel> variationModels, List<OrderProductRequest> orderProductList) {

        Integer totalProductPrice = 0;

        for (ProductVariationModel productVariationModel : variationModels) {

            Integer regularPrice = productVariationModel.getRegularPrice();
            Integer discountPrice = productVariationModel.getDiscountPrice();

            Integer productPrice;
            if (discountPrice != 0 && discountPrice < regularPrice) {
                productPrice = discountPrice;
            } else {
                productPrice = regularPrice;
            }

            Integer quantity = 0;

            for (OrderProductRequest productRequest : orderProductList) {
                if (productVariationModel.getProductModel().getProductSlug().equals(productRequest.getProductSlug())) {
                    if (productVariationModel.getStock() >= productRequest.getQuantity()) {
                        quantity = productRequest.getQuantity();
                        break;
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Not enough quantity of product: " + productVariationModel.getProductModel().getProductName());
                    }
                }
            }

            if (quantity == 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is a problem on product price " +
                        "calculation, with slug: " + productVariationModel.getProductModel().getProductSlug());
            } else {
                totalProductPrice += productPrice * quantity;
            }

        }

        return totalProductPrice;
    }


    private ProductVariationModel getProductVariationModel(ProductModel productModel, OrderProductRequest productRequest) {
        ProductVariationModel productVariationModel = null;

        for (ProductVariationModel productVariation : productModel.getVariations()) {
            if (productVariation.getId().equals((long) productRequest.getVariationId())) {
                productVariationModel = productVariation;
                break;
            }
        }

        if (productVariationModel == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Variation Not Matched");
        }
        return productVariationModel;
    }

    public CouponModel getCouponDiscount(Integer totalPrice, String couponCode) {
        Optional<CouponModel> couponModelOptional = couponRepository.findByCouponCode(couponCode);

        if (couponModelOptional.isPresent()) {
            CouponModel couponModel = couponModelOptional.get();
            int minimumBuy = couponModel.getMinimumBuy();
            long validFrom = couponModel.getValidFrom();
            long validUntil = couponModel.getValidTill();
            long currentTime = System.currentTimeMillis();

            if (currentTime < validUntil && currentTime > validFrom) {
                if (minimumBuy > totalPrice) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to buy minimum amount of: " + minimumBuy);
                } else {
                    return couponModel;
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon Time is not valid");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not found");
        }
    }

    public List<OrderProductModel> reduceStock(OrderPlaceRequest orderPlaceRequest, List<ProductVariationModel> productVariationModels, String username) {

        List<OrderProductModel> orderProductModels = new ArrayList<>();
        for (ProductVariationModel productVariationModel : productVariationModels) {

            for (OrderProductRequest productRequest : orderPlaceRequest.getProducts()) {
                ProductModel productModel = productVariationModel.getProductModel();

                if (productVariationModel.getStock() >= productRequest.getQuantity()) {       //Checking if productModel have enough quantity
                    productVariationModel.setStock(productVariationModel.getStock() - productRequest.getQuantity());    //Reduce Stock

                    if (productVariationModel.getStock() == 0) {    //Check if quantity is zero
                        productVariationModel.setInStock(false);     //Setting stock to false
                    }

                    productModel.setInStock(false);
                    for (ProductVariationModel variationModel : productModel.getVariations()) {
                        if (variationModel.getInStock()) {
                            productModel.setInStock(true);
                        }
                    }

                    productModel.setTotalSold(productModel.getTotalSold() + 1);       //Increasing total sold

                    //Adding new buyersId to product
                    Set<String> buyersId = productModel.getBuyersId();
                    buyersId.add(username);

                    productModel.setBuyersId(buyersId);

                    OrderProductModel orderProductModel = productToOrderProduct(productModel, productRequest, productVariationModel);
                    orderProductModels.add(orderProductModel);

                    break;
                } else {
                    //InCase Short Stock
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Not enough quantity of product: " + productModel.getProductName());
                }
            }

        }

        return orderProductModels;
    }


    //Migrating ProductModel to OrderProductModel
    private OrderProductModel productToOrderProduct(ProductModel productModel, OrderProductRequest productRequest,
                                                    ProductVariationModel productVariationModel) {

        StringBuilder variation = new StringBuilder();
        for (ProductAttributesModel productAttributesModel : productModel.getProductAttributeModels()) {
            for (ProductAttribute productAttribute : productAttributesModel.getAttributes()) {
                for (Long vId : productVariationModel.getAttributeCombinations()) {
                    if (productAttribute.getId().equals(vId)) {
                        variation.append(productAttribute.getValue()).append(" ");
                    }
                }
            }

        }

        Integer regularPrice = productVariationModel.getRegularPrice();
        Integer discountPrice = productVariationModel.getDiscountPrice();

        Integer productPrice;
        if (discountPrice != 0 && discountPrice < regularPrice) {
            productPrice = discountPrice;
        } else {
            productPrice = regularPrice;
        }

        OrderProductModel orderProductModel = new OrderProductModel();
        orderProductModel.setProductName(productModel.getProductName());
        orderProductModel.setProductSlug(productModel.getProductSlug());
        orderProductModel.setSKU(productModel.getSKU());
        orderProductModel.setBrand(productModel.getBrand());
        orderProductModel.setCategoryName(productModel.getCategoryModel().getCategoryName());
        orderProductModel.setSubCategoryName(productModel.getSubCategoryModel().getSubCategoryName());
        orderProductModel.setShortDescription(productModel.getShortDescription());
        orderProductModel.setDeliveryInfo(productModel.getDeliveryInfo());
        orderProductModel.setProductImages(productModel.getProductImages());
        orderProductModel.setQuantity(productRequest.getQuantity());
        orderProductModel.setVariation(variation.toString());
        orderProductModel.setPrice(productPrice);

        return orderProductModel;
    }

    public void affiliateFunctionalities(OrderPlaceRequest orderPlaceRequest, ProfileModel profileModel, List<ProductModel> productModels) {
        List<AffiliateUserTrackerModel> affiliateUserTrackerModels =
                affiliateUserTrackerRepository.findAllById(orderPlaceRequest.getAffiliateReferralIds()); //Get Al affiliate user trackers info

        for (AffiliateUserTrackerModel userTrackerModel : affiliateUserTrackerModels) {
            //Checking if any product from affiliate tracker matched with order products
            for (ProductModel productModel : productModels) {
                if (userTrackerModel.getProductSlug().equals(productModel.getProductSlug())) {
                    AffiliateUserModel affiliateUserModel = userTrackerModel.getAffiliateUserModel();

                    if (profileModel.getIsAffiliate() != null && profileModel.getAffiliateUser().getAffiliateUserSlug()
                            .equals(affiliateUserModel.getAffiliateUserSlug())) {
                        //If Order and Affiliate Referer is same then do nothing
                        continue;
                    }

                    if (affiliateUserModel.getProfileModel().getIsAffiliate()) {    //After matching, Getting the affiliate details
                        //Calculate the affiliate amount
                        Double affiliatePercentage = productModel.getAffiliatePercentage();
                        Integer affiliateAmount = (int) (productModel.getVariations().get(0).getRegularPrice() * affiliatePercentage / 100);

                        //Setting the data to affiliate user
                        Integer tempBalance = affiliateUserModel.getAffiliateBalance();
                        affiliateUserModel.setAffiliateBalance(tempBalance + affiliateAmount);
                        affiliateUserModel.setCompletedAffiliateProducts(
                                affiliateUserModel.getCompletedAffiliateProducts() + 1);
                        affiliateUserModel.setTotalSold(affiliateUserModel.getTotalSold() + productModel.getVariations().get(0).getRegularPrice());

                        affiliateUserRepository.save(affiliateUserModel);
                        affiliateUserTrackerRepository.delete(userTrackerModel);    //Delete the affiliate refer tracking info
                    }

                }
            }

        }
    }
}

//    public Integer getTotalProductPrice(List<ProductModel> productModels, List<OrderProductRequest> orderProductList) {
//
//        Integer totalProductPrice = 0;
//
//        for (ProductModel productModel : productModels) {
////            Integer regularPrice = productModel.getRegularPrice();
////            Integer discountPrice = productModel.getDiscountPrice();
//
//
//            Integer regularPrice = 0;
//            Integer discountPrice = 0;
//
//            Integer productPrice;
//
//
//            Integer quantity = 0;
//
//            for (OrderProductRequest productRequest : orderProductList) {
//                if (productModel.getProductSlug().equals(productRequest.getProductSlug())) {
//
//                    ProductVariationModel productVariationModel = getProductVariationModel(productModel, productRequest);
//
//                    if (productVariationModel.getStock() >= productRequest.getQuantity()) {
//                        quantity = productRequest.getQuantity();
//                        regularPrice = productVariationModel.getRegularPrice();
//                        discountPrice = productVariationModel.getDiscountPrice();
//                        break;
//                    } else {
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                                "Not enough quantity of product: " + productModel.getProductName());
//                    }
//
//                }
//            }
//
//            if (discountPrice != 0 && discountPrice < regularPrice) {
//                productPrice = discountPrice;
//            } else {
//                productPrice = regularPrice;
//            }
//
//            if (quantity == 0) {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is a problem on product price " +
//                        "calculation, with slug: " + productModel.getProductSlug());
//            } else {
//                totalProductPrice += productPrice * quantity;
//            }
//
//        }
//
//        return totalProductPrice;
//    }


    //Migrating ProductModel to OrderProductModel
//    private OrderProductModel productToOrderProduct(ProductModel productModel, OrderProductRequest productRequest) {
//
//        ProductVariationModel productVariationModel = getProductVariationModel(productModel, productRequest);
//
//        StringBuilder variation = new StringBuilder();
//        for (ProductAttributesModel productAttributesModel : productModel.getProductAttributeModels()) {
//            for (ProductAttribute productAttribute : productAttributesModel.getAttributes()) {
//                for (Integer vId : productVariationModel.getAttributeCombinations()) {
//                    if (productAttribute.getId().equals((long) vId)) {
//                        variation.append(productAttribute.getValue()).append(" ");
//                    }
//                }
//            }
//
//        }
//
//        OrderProductModel orderProductModel = new OrderProductModel();
//        orderProductModel.setProductName(productModel.getProductName());
//        orderProductModel.setProductSlug(productModel.getProductSlug());
//        orderProductModel.setSKU(productModel.getSKU());
//        orderProductModel.setBrand(productModel.getBrand());
//        orderProductModel.setCategoryName(productModel.getCategoryModel().getCategoryName());
//        orderProductModel.setSubCategoryName(productModel.getSubCategoryModel().getSubCategoryName());
//        orderProductModel.setShortDescription(productModel.getShortDescription());
//        orderProductModel.setDeliveryInfo(productModel.getDeliveryInfo());
//        orderProductModel.setProductImages(productModel.getProductImages());
//        orderProductModel.setQuantity(productRequest.getQuantity());
//        orderProductModel.setVariation(variation.toString());
//
//        return orderProductModel;
//    }

    //Migrate ProductModel to OrderProductModel and reduce product stock
//    public List<OrderProductModel> fromProductsToOrderProducts(List<ProductModel> productModels,
//                                                               List<OrderProductRequest> orderProductList, String userName) {
//
//        List<OrderProductModel> orderProductModels = new ArrayList<>();
//        for (ProductModel productModel : productModels) {        //Iterate over ProductModels
//
//            for (OrderProductRequest productRequest : orderProductList) {   //Iterate over OrderProductRequest
//
//                //Checking if current productModel equals orderProduct
//                if (productModel.getProductSlug().equals(productRequest.getProductSlug())) {
//
//                    ProductVariationModel productVariationModel = getProductVariationModel(productModel, productRequest);
//
//                    if (productVariationModel.getStock() >= productRequest.getQuantity()) {       //Checking if productModel have enough quantity
//                        productVariationModel.setStock(productVariationModel.getStock() - productRequest.getQuantity());    //Reduce Stock
//
//                        if (productVariationModel.getStock() == 0) {    //Check if quantity is zero
//                            productVariationModel.setInStock(false);     //Setting stock to false
//                        }
//                        productModel.setInStock(false);
//                        for (ProductVariationModel variationModel : productModel.getVariations()) {
//                            if (variationModel.getInStock()) {
//                                productModel.setInStock(true);
//                            }
//                        }
//
//                        productModel.setTotalSold(productModel.getTotalSold() + 1);       //Increasing total sold
//
//                        //Adding new buyersId to product
//                        Set<String> buyersId = productModel.getBuyersId();
//                        buyersId.add(userName);
//
//                        productModel.setBuyersId(buyersId);
//
//                        //Migrating ProductModel to OrderProductModel
//                        OrderProductModel orderProductModel = productToOrderProduct(productModel, productRequest);
//                        orderProductModels.add(orderProductModel);      //Adding to list
//
//                        break;
//                    } else {
//                        //InCase Short Stock
//                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                                "Not enough quantity of product: " + productModel.getProductName());
//                    }
//                }
//            }
//        }
//
//        return orderProductModels;
//    }