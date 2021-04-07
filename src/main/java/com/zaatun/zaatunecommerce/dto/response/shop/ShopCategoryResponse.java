package com.zaatun.zaatunecommerce.dto.response.shop;


import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopCategoryResponse {

    private String categoryName;

    private String categoryIcon;

    private String categorySlug;

    private String categoryImage;

    private List<ShopSubCategoryResponse> subCategories;
}
