package com.zaatun.zaatunecommerce.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_status_history_model")
public class OrderStatusHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String updateBy;

    private Long updatedOn;

    private String orderStatus;

    private String customerNote;

    private String employeeNote;

}
