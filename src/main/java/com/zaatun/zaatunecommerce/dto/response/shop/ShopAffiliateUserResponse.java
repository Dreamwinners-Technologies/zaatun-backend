package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopAffiliateUserResponse {


    private Boolean isApproved;

    private String username;

    private String affiliateUserSlug;

    private Integer affiliateBalance;

    private Integer completedAffiliateProducts;

    private Integer totalSold;

    List<ShopAffiliateWithdrawResponse> withdraws;
}
