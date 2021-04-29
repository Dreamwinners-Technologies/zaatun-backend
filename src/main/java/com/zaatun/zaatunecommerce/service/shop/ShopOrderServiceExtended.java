package com.zaatun.zaatunecommerce.service.shop;

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
    public Integer getTotalProductPrice(List<ProductModel> productModels, List<OrderProductRequest> orderProductList) {

        Integer totalProductPrice = 0;

        for (ProductModel productModel : productModels) {
            Integer regularPrice = productModel.getRegularPrice();
            Integer discountPrice = productModel.getDiscountPrice();

            Integer productPrice;

            if (discountPrice != 0 && discountPrice < regularPrice) {
                productPrice = discountPrice;
            } else {
                productPrice = regularPrice;
            }

            Integer quantity = 0;

            for (OrderProductRequest productRequest : orderProductList) {
                if (productModel.getProductSlug().equals(productRequest.getProductSlug())) {
                    if (productModel.getQuantity() >= productRequest.getQuantity()) {
                        quantity = productRequest.getQuantity();
                        break;
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Not enough quantity of product: " + productModel.getProductName());
                    }
                }
            }

            if (quantity == 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is a problem on product price " +
                        "calculation, with slug: " + productModel.getProductSlug());
            } else {
                totalProductPrice += productPrice * quantity;
            }

        }

        return totalProductPrice;
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

    //Migrate ProductModel to OrderProductModel and reduce product stock
    public List<OrderProductModel> fromProductsToOrderProducts(List<ProductModel> productModels,
                                                               List<OrderProductRequest> orderProductList, String userName) {

        List<OrderProductModel> orderProductModels = new ArrayList<>();
        for (ProductModel productModel : productModels) {        //Iterate over ProductModels

            for (OrderProductRequest productRequest : orderProductList) {   //Iterate over OrderProductRequest

                //Checking if current productModel equals orderProduct
                if (productModel.getProductSlug().equals(productRequest.getProductSlug())) {
                    if (productModel.getQuantity() >= productRequest.getQuantity()) {       //Checking if productModel have enough quantity
                        productModel.setQuantity(productModel.getQuantity() - productRequest.getQuantity());    //Reduce Stock

                        if (productModel.getQuantity() == 0) {    //Check if quantity is zero
                            productModel.setInStock(false);     //Setting stock to false
                        }
                        productModel.setTotalSold(productModel.getTotalSold() + 1);       //Increasing total sold

                        //Adding new buyersId to product
                        Set<String> buyersId = productModel.getBuyersId();
                        buyersId.add(userName);

                        productModel.setBuyersId(buyersId);

                        //Migrating ProductModel to OrderProductModel
                        OrderProductModel orderProductModel = productToOrderProduct(productModel);
                        orderProductModels.add(orderProductModel);      //Adding to list

                        break;
                    } else {
                        //InCase Short Stock
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Not enough quantity of product: " + productModel.getProductName());
                    }
                }
            }
        }

        return orderProductModels;
    }

    //Migrating ProductModel to OrderProductModel
    private OrderProductModel productToOrderProduct(ProductModel productModel) {
        OrderProductModel orderProductModel = new OrderProductModel();
        orderProductModel.setProductName(productModel.getProductName());
        orderProductModel.setProductSlug(productModel.getProductSlug());
        orderProductModel.setSKU(productModel.getSKU());
        orderProductModel.setBrand(productModel.getBrand());
        orderProductModel.setCategoryName(productModel.getCategoryModel().getCategoryName());
        orderProductModel.setSubCategoryName(productModel.getSubCategoryModel().getSubCategoryName());
        orderProductModel.setRegularPrice(productModel.getRegularPrice());
        orderProductModel.setDiscountPrice(productModel.getDiscountPrice());
        orderProductModel.setShortDescription(productModel.getShortDescription());
        orderProductModel.setDeliveryInfo(productModel.getDeliveryInfo());
        orderProductModel.setProductImages(productModel.getProductImages());
        orderProductModel.setQuantity(productModel.getQuantity());

        return orderProductModel;
    }
}
