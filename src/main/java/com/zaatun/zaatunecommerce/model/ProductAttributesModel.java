package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_attributes_model")
public class ProductAttributesModel {
    @Id
    private String productAttributeId;

    private String attributeName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ProductAttribute> attributes;
}
