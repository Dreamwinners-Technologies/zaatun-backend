package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_product_model")
public class OrderProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

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
