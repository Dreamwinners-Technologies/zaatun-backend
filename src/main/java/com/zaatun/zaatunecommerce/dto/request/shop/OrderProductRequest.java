package com.zaatun.zaatunecommerce.dto.request.shop;

import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductRequest {
    private String productSlug;

    private ProductVariantModel productVariant;

    private Integer quantity;

}
