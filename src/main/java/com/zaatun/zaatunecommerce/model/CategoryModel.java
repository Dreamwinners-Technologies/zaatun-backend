package com.zaatun.zaatunecommerce.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Entity
@Table(name = "category_model")
public class CategoryModel {
    @Id
    private String categoryId;

    private Long creationTime;

    private String createdBy;

    private Long updateTime;

    private String updateBy;

    private String categoryName;

    private String categoryIcon;

    private String categorySlug;

    private String categoryImage;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SubCategoryModel> subCategories;
}
