package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariantModel, Long> {
}
