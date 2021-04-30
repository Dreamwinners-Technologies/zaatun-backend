package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.AffiliateWithdrawModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffiliateWithdrawRepository extends JpaRepository<AffiliateWithdrawModel, Long> {
}
