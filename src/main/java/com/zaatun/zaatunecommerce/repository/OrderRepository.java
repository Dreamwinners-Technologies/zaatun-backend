package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, String> {
}
