package com.zaatun.zaatunecommerce.dto.response;

import com.zaatun.zaatunecommerce.model.AffiliateWithdrawModel;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateWithdrawResponse {
    ProfileModel profileModel;
    AffiliateWithdrawModel affiliateWithdraw;
}
