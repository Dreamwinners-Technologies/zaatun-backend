package com.zaatun.zaatunecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEditRequest {
    @NotBlank
    @Length(min = 4)
    private String productName;

    @NotBlank
    private String brand;

    private String categoryId;

    private String subCategoryId;

    private Integer buyingPrice;

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

    private List<ProductQuantityRequest> quantity;

    private String processor;

    private String battery;

    private String ram;

    private String rom;

    private String screenSize;

    private String backCamera;

    private String frontCamera;
}
