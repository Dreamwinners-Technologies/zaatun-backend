package com.zaatun.zaatunecommerce.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    private String productBadge;

    @NotBlank
    private String SKU;

    @NotBlank
    private String brand;

//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private CategoryModel categoryModel;

//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private SubCategoryModel subCategoryModel;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductAttributesModel> productAttributeModels;

//    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductVariationModel> variations;
}
