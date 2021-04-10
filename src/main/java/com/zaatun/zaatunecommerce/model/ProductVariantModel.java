package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_quantity_model")
public class ProductVariantModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String variant;

    private Integer quantity;

    private Boolean inStock;
}
