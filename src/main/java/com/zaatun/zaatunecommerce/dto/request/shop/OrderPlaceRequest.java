package com.zaatun.zaatunecommerce.dto.request.shop;

import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderPlaceRequest {

    private List<OrderProductRequest> products;

    private DeliveryAddressModel deliveryAddress;

    private String couponCode;

    private String paymentMethod;
}
