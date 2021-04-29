package com.zaatun.zaatunecommerce.repository;

import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressModel, Long> {

}
