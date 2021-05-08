package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductVariationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariationRepository extends JpaRepository<ProductVariationModel, Long> {
    List<ProductVariationModel> findByIdIn(List<Long> ids);
}
