package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;;import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopCategoryResponse {

    private String categoryName;

    private String categoryIcon;

    private String categorySlug;

    private String categoryImage;

    private List<ShopSubCategoryResponse> shopSubCategories;
}
