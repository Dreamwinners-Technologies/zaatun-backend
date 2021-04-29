package com.zaatun.zaatunecommerce.dto.request.shop;

import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderPlaceRequest {
    @NotEmpty
    @Valid
    private List<OrderProductRequest> products;

    @Positive
    private Long deliveryAddressId;

    @NotNull
    private String couponCode;

    private List<String> affiliateReferralIds;

    @NotBlank
    private String paymentMethod;

    private String orderComment;
}
