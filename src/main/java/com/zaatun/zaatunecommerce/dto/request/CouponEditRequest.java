package com.zaatun.zaatunecommerce.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponEditRequest {

    private Integer couponAmount;

    private Integer minimumBuy;

    private String couponDetails;

    private Long validFrom;

    private Long validTill;
}
