package com.zaatun.zaatunecommerce.model;

import lombok.*;

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

    private String reviewerUserName;

    private String reviewerName;

    private Integer reviewStar;

    private String comment;
}
