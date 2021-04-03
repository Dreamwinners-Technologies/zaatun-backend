package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryModel, String> {
}
