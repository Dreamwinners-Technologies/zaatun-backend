package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "category_model")
public class AffiliateUserModel {
    @Id
    private String id;

    private String affiliateUserId;

    private Integer affiliateBalance;

    private Integer completedAffiliateProducts;


}
