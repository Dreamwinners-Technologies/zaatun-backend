package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductModel, String> {
    Optional<ProductModel> findByProductSlug(String productSlug);

    List<ProductModel> findByProductSlugIn(List<String> productSlugs);
}
