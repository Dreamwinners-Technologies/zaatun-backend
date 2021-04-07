package com.zaatun.zaatunecommerce.dto.response.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

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
