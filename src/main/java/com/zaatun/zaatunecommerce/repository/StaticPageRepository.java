package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.StaticPageModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaticPageRepository extends JpaRepository<StaticPageModel, String> {
    Optional<StaticPageModel> findByPageSlug(String pageSlug);
}
