package com.zaatun.zaatunecommerce.dto.request;

import com.zaatun.zaatunecommerce.model.OrderStatuses;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class OrderProcessRequest {

    @NotEmpty
    @NotNull
    private OrderStatuses orderStatus;

    @NotEmpty
    @NotNull
    private String customerNote;

    @NotEmpty
    @NotNull
    private String employeeNote;

}
