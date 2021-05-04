package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feature_box_model")
public class FeatureBoxModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long updatedOn;

    private String updatedBy;

    @Column(unique = true)
    private Integer sequenceNo;

    private String title;

    private String subTitle;

    private String link;

    private String bgColor;

    private String imageLink;

    private String showButton;

}
