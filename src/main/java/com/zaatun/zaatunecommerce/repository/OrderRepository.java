package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<OrderModel, String> {
    Page<OrderModel> findByUserName(String userName, Pageable pageable);

    Optional<OrderModel> findByOrderId(String orderId);

    Optional<OrderModel> findByTransactionId(String transactionId);

    long countByOrderStatus(String orderStatus);
}
