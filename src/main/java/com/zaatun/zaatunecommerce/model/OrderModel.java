package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
//@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_model")
public class OrderModel {
    @Id
    private String id;

    private String orderId;

    private String invoiceId;

    private String createBy;

    private Long createdOn;

    private String updatedBy;

    private Long updatedOn;

    private String userName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderProductModel> orderItems;

    @OneToOne(cascade = CascadeType.ALL)
    private DeliveryAddressModel deliveryAddress;

    private String orderStatus;

    private Integer productPriceTotal;

    private Integer paidAmount;

    private String paymentMethod;

    private String paymentStatus;

    private Integer shippingCharge;

    private Integer adminDiscount;

    private String adminDiscountAddedBy;

    private Integer subTotal;

    private Integer totalAmount;

    private String transactionId;

    private Integer couponDiscount;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private CouponModel couponModel;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderProcessHistoryModel> orderProcessHistory;

    private Boolean isCompleted;

    private String orderComment;

    private String riskLevel;

    private String riskTitle;

}
