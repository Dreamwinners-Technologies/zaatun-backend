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
public class ShopProductResponseV2 {
    private String productName;

    private String productSlug;

    private String productBadge;

    private String brand;

    private String shortDescription;

    private Boolean inStock;

    private List<String> productImages;

    private List<ShopVariationResponse> variations;

    private Double reviewStar;

}
