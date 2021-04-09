package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private ProductModel product;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private ProductVariantModel productVariant;

    private Integer quantity;

}
