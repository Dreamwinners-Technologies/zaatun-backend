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
@Table(name = "delivery_address_model")
public class DeliveryAddressModel {
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
