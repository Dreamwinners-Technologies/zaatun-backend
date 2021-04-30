package com.zaatun.zaatunecommerce.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profile_model")
public class ProfileModel {

    @Id
    private String id;

    @Column(unique = true)
    @NaturalId(mutable = true)
    @Size(min = 11, max = 11)
    String phoneNo;

    @Column(unique = true)
    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @Column(unique = true)
    @NaturalId(mutable = true)
    @Size(min = 11,max = 50)
    @Email
    private String email;

    private String address;

    private String dateOfBirth;

    private String thana;

    private String district;

    @OneToMany(cascade = CascadeType.ALL)
    private List<DeliveryAddressModel> deliveryAddresses;

    private Integer totalOrders;

    private Integer totalOrderAmounts;

    private Boolean isAffiliate;

    @OneToOne(cascade = CascadeType.ALL)
    private AffiliateUserModel affiliateUser;

}
