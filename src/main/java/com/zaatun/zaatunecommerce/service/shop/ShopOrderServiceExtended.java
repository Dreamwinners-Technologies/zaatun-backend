package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.request.shop.OrderProductRequest;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProcessHistory;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.*;
import com.zaatun.zaatunecommerce.service.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    Integer calculateTotalProductPrice(List<OrderProductRequest> products) {
        Integer productPriceTotal = 0;

        for (OrderProductRequest orderProductRequest : products) {
            String productSlug = orderProductRequest.getProductSlug();
            Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(productSlug);
            if (productModelOptional.isPresent()) {
                ProductModel productModel = productModelOptional.get();
                Integer discountPrice = productModel.getDiscountPrice();
                Integer regularPrice = productModel.getRegularPrice();
                Integer quantity = orderProductRequest.getQuantity();
                if (discountPrice != null && discountPrice != 0) {
                    productPriceTotal += discountPrice * quantity;
                } else {
                    productPriceTotal += regularPrice * quantity;
                }
            }
        }

        return productPriceTotal;
    }

    Integer calculateTotal(Integer productPriceTotal, Integer adminDiscount, Integer couponDiscount) {
        Integer total = productPriceTotal - adminDiscount;
        total -= couponDiscount;

        return total;
    }

    Optional<CouponModel> calculateCouponDiscount(String couponCode, Integer productPriceTotal) {
        if (couponCode == null) {
            return Optional.empty();
        } else if (couponCode.isEmpty()) {
            return Optional.empty();
        } else {
            Optional<CouponModel> couponModelOptional = couponRepository.findByCouponCode(couponCode);

            if (couponModelOptional.isPresent()) {
                CouponModel couponModel = couponModelOptional.get();
                long currentTime = System.currentTimeMillis();

                if (currentTime >= couponModel.getValidFrom() && currentTime <= couponModel.getValidTill()) {
                    if (productPriceTotal >= couponModel.getMinimumBuy()) {
                        return Optional.of(couponModel);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to buy minimum amount " +
                                couponModel.getMinimumBuy() + "Taka to avail this coupon.");
                    }
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon Time expired");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon Code is not valid");
            }
        }


    }

    Integer calculateShippingCharge(DeliveryAddressModel deliveryAddress) {

        return 100;
    }

    private Integer calculateProductPriceTotal(List<OrderProductModel> orderProductModelList) {
        Integer productPriceTotal = 0;

        for (OrderProductModel orderProductModel : orderProductModelList) {
            Integer discountPrice = orderProductModel.getProduct().getDiscountPrice();
            Integer regularPrice = orderProductModel.getProduct().getRegularPrice();
            Integer quantity = orderProductModel.getQuantity();
            if (discountPrice != null && discountPrice != 0) {
                productPriceTotal += discountPrice * quantity;
            } else {
                productPriceTotal += regularPrice * quantity;
            }
        }

        return productPriceTotal;
    }

    List<OrderProductModel> fromProductIdListToProductList(List<OrderProductRequest> orderProductRequestList, String token) {
        List<OrderProductModel> orderProductModelList = new ArrayList<>();

        for (OrderProductRequest orderProductRequest : orderProductRequestList) {
            OrderProductModel orderProductModel = new OrderProductModel();
            Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(orderProductRequest.getProductSlug());

            if (productModelOptional.isPresent()) {
                ProductModel productModel = productModelOptional.get();

                List<ProductVariantModel> productVariantModelList = productModel.getVariants();
                List<Long> productVariantIds = productVariantIdListFromProductVariants(productVariantModelList);
                ProductVariantModel productVariantModelDb = new ProductVariantModel();

                if (productVariantIds.contains(orderProductRequest.getProductVariantId())) {

                    boolean checkAllStock = false;
                    for (ProductVariantModel productVariantModel : productVariantModelList) {
                        if (productVariantModel.getId().equals(orderProductRequest.getProductVariantId())) {
                            Integer productQuantityInDb = productVariantModel.getQuantity();
                            Integer productQuantityInOrder = orderProductRequest.getQuantity();
                            if (productQuantityInDb >= productQuantityInOrder) {
                                productQuantityInDb -= productQuantityInOrder;
                                productVariantModel.setQuantity(productQuantityInDb);
                                productModel.setTotalSold(productModel.getTotalSold() + productQuantityInOrder);
                                if (productQuantityInDb == 0) {
                                    productVariantModel.setInStock(false);
                                }

                                productVariantModelDb = productVariantModel;
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
                    Set<String> buyers = productModel.getBuyersId();
                    buyers.add(jwtProvider.getUserNameFromJwt(token));
                    productModel.setBuyersId(buyers);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Variant is not matched with product");
                }
                orderProductModel.setProduct(productModel);
                orderProductModel.setProductVariant(productVariantModelDb);
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

    private List<Long> productVariantIdListFromProductVariants(List<ProductVariantModel> productVariantModelList) {
        List<Long> productVariantIdList = new ArrayList<>();

        for (ProductVariantModel productVariantModel : productVariantModelList) {
            productVariantIdList.add(productVariantModel.getId());
        }
        return productVariantIdList;
    }


    List<ShopOrderProcessHistory> orderProcessHistoryForShop(OrderModel orderModel) {
        List<ShopOrderProcessHistory> shopOrderProcessHistories = new ArrayList<>();
        for (OrderProcessHistoryModel orderProcessHistory: orderModel.getOrderProcessHistory()){
            ShopOrderProcessHistory shopOrderProcessHistory =
                    new ShopOrderProcessHistory(orderProcessHistory.getUpdateBy(), orderProcessHistory.getUpdatedOn(),
                            orderProcessHistory.getOrderStatus(), orderProcessHistory.getCustomerNote());

            shopOrderProcessHistories.add(shopOrderProcessHistory);
        }
        return shopOrderProcessHistories;
    }
}
