package com.zaatun.zaatunecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variation_model")
public class ProductVariationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stock;

    private Boolean inStock;

    private Boolean isDefault;

    @Column(nullable = false)
    private Integer buyingPrice;

    @Min(1)
    @Column(nullable = false)
    private Integer regularPrice;

    @Column(nullable = false)
    private Integer discountPrice;

    @CollectionTable
    @ElementCollection
    private List<Long> attributeCombinations;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private ProductModel productModel;
}
