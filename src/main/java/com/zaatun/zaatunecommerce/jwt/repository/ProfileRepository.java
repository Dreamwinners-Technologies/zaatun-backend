package com.zaatun.zaatunecommerce.jwt.repository;

import com.zaatun.zaatunecommerce.jwt.model.ProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileModel, String> {
    boolean existsByPhoneNo(String phoneNo);

    boolean existsByEmail(String email);

    Optional<ProfileModel> findByUsername(String username);
}
