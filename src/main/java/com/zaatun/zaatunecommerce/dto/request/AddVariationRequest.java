package com.zaatun.zaatunecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddVariationRequest {
    private Integer stock;

    private Boolean inStock;

    private Boolean isDefault;

    @Min(0)
    @Column(nullable = false)
    private Integer buyingPrice;

    @Min(1)
    @Column(nullable = false)
    private Integer regularPrice;

    @Min(0)
    @Column(nullable = false)
    private Integer discountPrice;

    private List<Long> attributeCombinations;
}
