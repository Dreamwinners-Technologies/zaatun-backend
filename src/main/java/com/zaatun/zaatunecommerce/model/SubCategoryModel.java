package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
@Table(name = "sub_category_model")
public class SubCategoryModel {
    @Id
    private String subCategoryId;

    private Long creationTime;

    private String createdBy;

    private Long updateTime;

    private String updateBy;

    private String subCategoryName;

    private String subCategoryIcon;

    private String subCategorySlug;

    private String subCategoryImage;
}
