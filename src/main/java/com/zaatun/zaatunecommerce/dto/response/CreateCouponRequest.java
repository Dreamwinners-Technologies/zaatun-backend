package com.zaatun.zaatunecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCouponRequest {

    private String couponCode;

    private Integer couponAmount;

    private Integer minimumBuy;

    private String couponDetails;

    private Long validFrom;

    private Long validTill;
}
