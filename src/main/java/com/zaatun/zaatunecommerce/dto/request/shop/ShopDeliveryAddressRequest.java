package com.zaatun.zaatunecommerce.dto.request.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopDeliveryAddressRequest {
    private String fullName;

    private String phoneNo;

    private String address;

    private String area;

    private String city;

    private String region;
}
