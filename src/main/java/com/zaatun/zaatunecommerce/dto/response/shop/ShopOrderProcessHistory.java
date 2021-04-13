package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopOrderProcessHistory {
    private String updateBy;

    private Long updatedOn;

    private String orderStatus;

    private String customerNote;
}
