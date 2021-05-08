package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopVariationResponse {

    private Long id;

    private Integer stock;

    private Boolean inStock;

    private Boolean isDefault;

    private Integer regularPrice;

    private Integer discountPrice;

    private List<Long> attributeCombinations;
}
