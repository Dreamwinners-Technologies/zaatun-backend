package com.zaatun.zaatunecommerce.dto.response.shop;

import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopProductResponse {

    private String productName;

    private String productSlug;

    private String SKU;

    private String brand;

    private ShopCategoryResponse category;

    private ShopSubCategoryResponse subCategory;

    private Integer regularPrice;

    private Integer discountPrice;

    private String description;

    private String shortDescription;

    private String warranty;

    private String emi;

    private Boolean inStock;

    private Boolean isFeatured;

    private Boolean isAvailable;

    private String videoUrl;

    private Double affiliatePercentage;

    private Double vat;

    private List<String> productImages;

    private List<ProductVariantModel> quantity;

    private List<ProductReviewModel> productReviews;

    private String processor;

    private String battery;

    private String ram;

    private String rom;

    private String screenSize;

    private String backCamera;

    private String frontCamera;
}
