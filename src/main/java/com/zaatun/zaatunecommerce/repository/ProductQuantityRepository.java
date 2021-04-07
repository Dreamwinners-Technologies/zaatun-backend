package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductQuantityModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductQuantityRepository extends JpaRepository<ProductQuantityModel, Long> {
}
