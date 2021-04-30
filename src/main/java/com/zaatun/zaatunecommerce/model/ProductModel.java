package com.zaatun.zaatunecommerce.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_model")
public class ProductModel {
    @Id
    private String productId;

    private String createdBy;

    private Long createdOn;

    private String updatedBy;

    private Long updatedOn;

    @NotBlank
    @Length(min = 4)
    private String productName;

    private String productSlug;

    @NotBlank
    private String SKU;

    @NotBlank
    private String brand;

    @ManyToOne(cascade = CascadeType.ALL)
    private CategoryModel categoryModel;

    @ManyToOne(cascade = CascadeType.ALL)
    private SubCategoryModel subCategoryModel;

    @Column(nullable = false)
    private Integer buyingPrice;

    @Min(1)
    @Column(nullable = false)
    private Integer regularPrice;

    @Column(nullable = false)
    private Integer discountPrice;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(columnDefinition="TEXT")
    private String shortDescription;

    @Column(columnDefinition="TEXT")
    private String warranty;

    private String emi;

    private Boolean inStock;

    private Boolean isFeatured;

    private Boolean isDiscount;

    private String videoUrl;

    private Double affiliatePercentage;

    private Double vat;

    private Long totalSold;

    private Integer quantity;

    private String deliveryInfo;

    private String key;

    private String value;

    private Double rating;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductReviewModel> productReviews;

    @CollectionTable
    @ElementCollection
    private Set<String> buyersId;

    @OneToOne(cascade = CascadeType.ALL)
    private SpecificationModel specification;

    @CollectionTable
    @ElementCollection
    private List<String> productImages;
}
