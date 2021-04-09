package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.OrderProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProductModel, Long> {
}
