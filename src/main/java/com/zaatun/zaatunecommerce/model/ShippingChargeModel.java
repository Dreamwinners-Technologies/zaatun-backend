package com.zaatun.zaatunecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipping_charge_model")
public class ShippingChargeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String updateBy;

    private Long updateOn;

    private String area;

    private Integer amount;
}
