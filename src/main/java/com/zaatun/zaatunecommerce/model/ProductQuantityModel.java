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
@Table
public class ProductQuantityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String variant;

    private Long quantity;
}
