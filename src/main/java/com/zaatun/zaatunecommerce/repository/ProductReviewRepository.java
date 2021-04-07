package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReviewModel, Long> {
}
