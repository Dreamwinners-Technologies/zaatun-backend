package com.zaatun.zaatunecommerce.model;

import liquibase.pro.packaged.E;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "copun_model")
public class CouponModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long couponId;

    private String couponCode;

    private String createdBy;

    private Long createdOn;

    private String updatedBy;

    private Long updatedOn;

    private Integer couponAmount;

    private Integer minimumBuy;

    private String couponDetails;

    private Long validFrom;

    private Long validTill;
}
