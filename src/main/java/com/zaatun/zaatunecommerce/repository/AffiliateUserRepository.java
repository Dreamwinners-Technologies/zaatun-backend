package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.AffiliateUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateUserRepository extends JpaRepository<AffiliateUserModel, String> {
    Optional<AffiliateUserModel> findByAffiliateUserSlug(String affiliateUserSlug);
}
