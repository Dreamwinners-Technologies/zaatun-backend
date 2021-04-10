package com.zaatun.zaatunecommerce.dto.request.shop;

import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class OrderProductRequest {
    @NotBlank
    private String productSlug;

    @Positive
    private Long productVariantId;

    @Positive
    private Integer quantity;

}
