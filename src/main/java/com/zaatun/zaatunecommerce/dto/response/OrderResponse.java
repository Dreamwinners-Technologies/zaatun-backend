package com.zaatun.zaatunecommerce.dto.response;

import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProductResponse;
import com.zaatun.zaatunecommerce.model.CouponModel;
import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import com.zaatun.zaatunecommerce.model.OrderProcessHistoryModel;
import com.zaatun.zaatunecommerce.model.OrderProductModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;

    private String orderId;

    private String invoiceId;

    private String createBy;

    private Long createdOn;

    private String updatedBy;

    private Long updatedOn;

    private String userName;

    private List<ShopOrderProductResponse> orderItems;

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

    private List<OrderProcessHistoryModel> orderProcessHistory;

    private Boolean isCompleted;
}
