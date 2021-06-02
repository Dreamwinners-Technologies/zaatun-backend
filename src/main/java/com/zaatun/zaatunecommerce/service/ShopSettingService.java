package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.HomepageCategoriesRequest;
import com.zaatun.zaatunecommerce.model.HomePageCategoriesModel;
import com.zaatun.zaatunecommerce.repository.HomePageCategoriesRepository;
import com.zaatun.zaatunecommerce.utils.ImageUtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ShopSettingService {
    private final HomePageCategoriesRepository homePageCategoriesRepository;
    private final ImageUtilService imageUtilService;

    public ResponseEntity<ApiResponse<Long>> addHomepageCategory(HomepageCategoriesRequest homepageCategoriesRequest) {

        HomePageCategoriesModel homePageCategoriesModel =
                new HomePageCategoriesModel(null, homepageCategoriesRequest.getCategoryId(), null);

        homePageCategoriesModel = homePageCategoriesRepository.save(homePageCategoriesModel);

        return new ResponseEntity<>(new ApiResponse<>(200, "OK", homePageCategoriesModel.getId()), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<String>> addHomepageCategoryImage(Long id, MultipartFile mpFile) {
        Optional<HomePageCategoriesModel> homePageCategoriesModelOptional = homePageCategoriesRepository.findById(id);

        if (homePageCategoriesModelOptional.isPresent()) {
            HomePageCategoriesModel homePageCategoriesModel = homePageCategoriesModelOptional.get();

            MultipartFile[] multipartFiles = new MultipartFile[1];
            multipartFiles[0] = mpFile;
            String imageLink = imageUtilService.uploadImage(multipartFiles).get(0);
            homePageCategoriesModel.setImage(imageLink);

            homePageCategoriesRepository.save(homePageCategoriesModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "OK", imageLink), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found with id: " + id);
        }
    }

    public ResponseEntity<ApiResponse<List<HomePageCategoriesModel>>> getHomepageCategories() {
        List<HomePageCategoriesModel> homePageCategoriesModels = homePageCategoriesRepository.findAll();

        return new ResponseEntity<>(new ApiResponse<>(200, "OK", homePageCategoriesModels), HttpStatus.OK);
    }
}
