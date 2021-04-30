package com.zaatun.zaatunecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "affiliate_withdraw_model")
public class AffiliateWithdrawModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long createdOn;

    private Long updatedOn;

    private String updatedBy;

    private Integer withdrawAmount;

    private String massage;

    private Boolean isApproved;

    private Boolean isCompleted;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private AffiliateUserModel affiliateUserModel;

}
