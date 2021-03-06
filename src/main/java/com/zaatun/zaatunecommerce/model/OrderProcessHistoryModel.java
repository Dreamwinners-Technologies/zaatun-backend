package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_status_history_model")
public class OrderProcessHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String updateBy;

    private Long updatedOn;

    private String orderStatus;

    private String customerNote;

    private String employeeNote;

}
