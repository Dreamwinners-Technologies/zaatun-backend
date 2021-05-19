package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.ShortStatisticsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortStatisticsRepository extends JpaRepository<ShortStatisticsModel, Integer> {
}
