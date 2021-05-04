package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.AffiliateUserTrackerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffiliateUserTrackerRepository extends JpaRepository<AffiliateUserTrackerModel, Long> {

}
