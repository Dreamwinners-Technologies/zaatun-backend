package com.zaatun.zaatunecommerce.dto.request.shop;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class OrderProductRequest {
    @NotBlank
    private String productSlug;

    @Positive(message = "Quantity can't be less than 1")
    private Integer quantity;

    private Long variationId;

}
