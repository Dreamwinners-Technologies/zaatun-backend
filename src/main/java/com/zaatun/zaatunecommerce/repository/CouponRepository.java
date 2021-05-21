package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.CouponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponModel, String> {
    Optional<CouponModel> findByCouponCode(String couponCode);
}
