package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.FeatureBoxModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureBoxRepository extends JpaRepository<FeatureBoxModel, Long> {
}
