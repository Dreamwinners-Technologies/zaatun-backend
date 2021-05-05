package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductModel;
import liquibase.pro.packaged.S;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductModel, String> {
    Optional<ProductModel> findByProductSlug(String productSlug);

    List<ProductModel> findByProductSlugIn(List<String> productSlugs);

//    Page<ProductModel> findAll(Example<S> example, Pageable pageable);
}
