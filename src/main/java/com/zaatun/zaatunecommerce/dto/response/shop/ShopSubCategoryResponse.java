package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopSubCategoryResponse {

    private String subCategoryName;

    private String subCategoryIcon;

    private String subCategorySlug;

    private String subCategoryImage;
}
