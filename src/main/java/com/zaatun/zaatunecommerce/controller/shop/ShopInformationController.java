package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.FeatureBoxModel;
import com.zaatun.zaatunecommerce.service.shop.ShopInformationService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store")
public class ShopInformationController {
    private final ShopInformationService shopInformationService;

    @GetMapping("/featureBox")
    public ResponseEntity<ApiResponse<List<FeatureBoxModel>>> getFeatureBoxList(){
        return shopInformationService.getFeatureBoxList();
    }

}
