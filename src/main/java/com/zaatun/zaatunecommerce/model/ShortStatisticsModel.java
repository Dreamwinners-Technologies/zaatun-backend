package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "short_stat_model")
public class ShortStatisticsModel {
    @Id
    private Integer id;

    private Integer totalOrders;

    private Integer pendingOrders;

    private Integer processingOrders;

    private Integer shippedOrders;

    private Integer deliveredOrders;

    private Integer canceledOrders;

    private Integer postponedOrders;

    private Integer newAffiliateUserRequests;

    private Integer affiliateWithdrawPending;

    private Integer affiliateWithdrawCancelled;

    private Integer affiliateWithdrawCompleted;

    private Integer totalProducts;

    private Integer totalUsers;
}