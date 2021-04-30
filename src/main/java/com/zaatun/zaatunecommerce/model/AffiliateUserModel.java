package com.zaatun.zaatunecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "affiliate_user_model")
public class AffiliateUserModel {
    @Id
    private String id;

    private Long createdOn;

    private String updatedBy;

    private Long updatedOn;

    private Boolean isApproved;

    private String username;

    private String affiliateUserSlug;

    private Integer affiliateBalance;

    private Integer completedAffiliateProducts;

    private Integer totalSold;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AffiliateWithdrawModel> affiliateWithdrawModels;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private ProfileModel profileModel;
}
