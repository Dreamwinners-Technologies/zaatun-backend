package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.HomepageCategoriesRequest;
import com.zaatun.zaatunecommerce.model.HomePageCategoriesModel;
import com.zaatun.zaatunecommerce.service.ShopSettingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ShopSettingController {
    private final ShopSettingService shopSettingService;

    @PostMapping("/dashboard/homepageCategories")
    public ResponseEntity<ApiResponse<Long>> addHomepageCategory(@RequestBody HomepageCategoriesRequest homepageCategoriesRequest){
        return shopSettingService.addHomepageCategory(homepageCategoriesRequest);
    }

    @PostMapping("/dashboard/homepageCategories/image/{id}")
    public ResponseEntity<ApiResponse<String>> addHomepageCategoryImage(@PathVariable Long id,
                                                                        MultipartFile mpFile){
        return shopSettingService.addHomepageCategoryImage(id, mpFile);
    }

    @GetMapping("/dashboard/homepageCategories")
    public ResponseEntity<ApiResponse<List<HomePageCategoriesModel>>> getHomepageCategories(){
        return shopSettingService.getHomepageCategories();
    }
}
