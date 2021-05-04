package com.zaatun.zaatunecommerce.dto.response.shop;

import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import com.zaatun.zaatunecommerce.model.SpecificationModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopProductResponse {

    private String productName;

    private String productSlug;

    private String productBadge;

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

    private Boolean isDiscount;

    private String videoUrl;

    private Double vat;

    private List<String> productImages;

    private Integer quantity;

    private List<ProductReviewModel> productReviews;

    private Set<String> buyersId;

    private SpecificationModel specification;

    private String referralId;

}
