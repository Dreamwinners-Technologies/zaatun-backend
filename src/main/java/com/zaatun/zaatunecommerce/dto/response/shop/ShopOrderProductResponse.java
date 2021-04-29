package com.zaatun.zaatunecommerce.dto.response.shop;

import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopOrderProductResponse {
    private String productName;

    private String productSlug;

    private String SKU;

    private String brand;

    private String categoryName;

    private String subCategoryName;

    private Integer regularPrice;

    private Integer discountPrice;

    private String shortDescription;

    private Double vat;

    private String deliveryInfo;

    @CollectionTable
    @ElementCollection
    private List<String> productImages;

    private Integer quantity;

}
