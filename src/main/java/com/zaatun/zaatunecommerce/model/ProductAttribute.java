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
@Table(name = "product_attribute")
public class ProductAttribute{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String value;
}
