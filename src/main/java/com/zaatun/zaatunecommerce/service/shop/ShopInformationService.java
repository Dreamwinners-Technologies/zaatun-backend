package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.FeatureBoxModel;
import com.zaatun.zaatunecommerce.repository.FeatureBoxRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.support.FactoryBeanRegistrySupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShopInformationService {
    private final FeatureBoxRepository featureBoxRepository;

    public ResponseEntity<ApiResponse<List<FeatureBoxModel>>> getFeatureBoxList() {
        List<FeatureBoxModel> featureBoxModels = featureBoxRepository.findAll();

        if (featureBoxModels.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Feature Box Found", featureBoxModels),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Feature Box Found", featureBoxModels),
                    HttpStatus.OK);
        }
    }
}
