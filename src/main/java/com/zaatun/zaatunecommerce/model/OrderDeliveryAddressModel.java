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
@Table(name = "order_delivery_address_model")
public class OrderDeliveryAddressModel {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long addressId;

    private String fullName;

    private String phoneNo;

    private String address;

    private String area;

    private String city;

    private String region;
}
