package com.zaatun.zaatunecommerce.model;

import com.zaatun.zaatunecommerce.dto.response.shop.ShopVariationResponse;
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

    @Column(columnDefinition="TEXT")
    private String shortDescription;

    private Double vat;

    @Column(columnDefinition="TEXT")
    private String deliveryInfo;

    @CollectionTable
    @ElementCollection
    private List<String> productImages;

    private Integer quantity;

    @Column(columnDefinition="TEXT")
    private String variation;

    private Integer price;

}

