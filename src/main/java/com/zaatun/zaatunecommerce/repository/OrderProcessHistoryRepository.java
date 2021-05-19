package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.OrderProcessHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProcessHistoryRepository extends JpaRepository<OrderProcessHistoryModel, Long> {
    long countByOrderStatus(String orderStatus);
}
