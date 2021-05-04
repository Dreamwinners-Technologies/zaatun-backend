package com.zaatun.zaatunecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "feature_box_model")
public class FeatureBoxModel {
    @Id
    private Long id;

    private Integer sequenceNo;

    private String title;

    private String subTitle;

    private String link;

    private String bgColor;

    private String imageLink;

}
