package com.zaatun.zaatunecommerce.dto.response.shop;


import com.zaatun.zaatunecommerce.model.ProductAttributesModel;
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

    private List<ProductAttributesModel> attributes;

    private ShopCategoryResponse category;

    private ShopSubCategoryResponse subCategory;

}
