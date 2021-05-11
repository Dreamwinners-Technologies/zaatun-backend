package com.zaatun.zaatunecommerce.dto.request.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddReviewRequest {
    private Integer reviewStar;

    private String comment;
}
