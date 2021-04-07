package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends JpaRepository<ProductModel, String> {

}
