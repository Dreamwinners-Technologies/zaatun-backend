package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "affiliate_user_tracker_model")
public class AffiliateUserTrackerModel {
    @Id
    private String referralId;

    private String productSlug;

//    private String affiliateUserSlug;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private AffiliateUserModel affiliateUserModel;

}
