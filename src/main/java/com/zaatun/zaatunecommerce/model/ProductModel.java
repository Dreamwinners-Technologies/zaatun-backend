package com.zaatun.zaatunecommerce.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

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

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private CategoryModel categoryModel;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private SubCategoryModel subCategoryModel;

    private Integer buyingPrice;

    private Integer regularPrice;

    private Integer discountPrice;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(columnDefinition="TEXT")
    private String shortDescription;

    private String warranty;

    private String emi;

    private Boolean inStock;

    private Boolean isFeatured;

    private Boolean isAvailable;

    private String videoUrl;

    private Double affiliatePercentage;

    private Double vat;

    private Long totalSold;

    @CollectionTable
    @ElementCollection
    private List<String> productImages;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductQuantityModel> quantity;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductReviewModel> productReviews;

    @CollectionTable
    @ElementCollection
    private List<String> buyersId;

    private String processor;

    private String battery;

    private String ram;

    private String rom;

    private String screenSize;

    private String backCamera;

    private String frontCamera;

}
