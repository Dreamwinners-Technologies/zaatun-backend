package com.zaatun.zaatunecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class OrderProcessRequest {
    private String orderStatus;

    private String customerNote;

    private String employeeNote;

}
