package com.zaatun.zaatunecommerce.dto.request;

import com.zaatun.zaatunecommerce.model.OrderStatuses;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProcessRequest {

    private OrderStatuses orderStatus;

    private String customerNote;

    private String employeeNote;

}
