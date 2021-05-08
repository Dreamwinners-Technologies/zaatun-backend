package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopAffiliateWithdrawResponse {

    private Integer withdrawAmount;

    private String massage;

    private Boolean isApproved;

    private Boolean isCompleted;

}
