package com.zaatun.zaatunecommerce.dto.response.shop;

import com.zaatun.zaatunecommerce.model.CouponModel;
import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import com.zaatun.zaatunecommerce.model.OrderProcessHistoryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderResponse {
    private String orderId;

    private String invoiceId;

    private String userName;

    private List<ShopProductResponse> orderItems;

    private DeliveryAddressModel deliveryAddress;

    private String orderStatus;

    private Integer productPriceTotal;

    private Integer paidAmount;

    private String paymentMethod;

    private String paymentStatus;

    private Integer shippingCharge;

    private Integer adminDiscount;

    private String adminDiscountAddedBy;

    private Integer couponDiscount;

    private Integer subTotal;

    private Integer totalAmount;

    private String transactionId;

    private CouponModel couponModel;

    private List<ShopOrderProcessHistory> orderProcessHistory;
}
