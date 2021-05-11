package com.zaatun.zaatunecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_review_model")
public class ProductReviewModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    private Long createdOn;

    private String reviewerUserName;

    private String reviewerName;

    private Integer reviewStar;

    private String comment;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private ProductModel productModel;
}
