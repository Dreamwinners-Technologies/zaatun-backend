package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "copun_model")
public class CouponModel {
    @Id
    private String couponId;

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
