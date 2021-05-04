package com.zaatun.zaatunecommerce.dto.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddProductRequest {

    @NotBlank
    @Length(min = 4)
    private String productName;

    @NotBlank
    private String brand;

    private String productBadge;

    private String categoryId;

    private String subCategoryId;

    @Min(0)
    @Column(nullable = false)
    private Integer buyingPrice;

    @Min(1)
    @Column(nullable = false)
    private Integer regularPrice;

    @Min(0)
    @Column(nullable = false)
    private Integer discountPrice;

    private String description;

    private String shortDescription;

    private String warranty;

    private String emi;

    private Boolean inStock;

    private Boolean isFeatured;

    private Boolean isDiscount;

    private String videoUrl;

    private Double affiliatePercentage;

    private Double vat;

    private Integer quantity;

    private String deliveryInfo;

    private String key;

    private String value;

    private AddSpecificationRequest addSpecification;

}
