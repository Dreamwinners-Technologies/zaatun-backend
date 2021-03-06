package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.CategoryImageEnum;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import com.zaatun.zaatunecommerce.repository.SubCategoryRepository;
import com.zaatun.zaatunecommerce.utils.ImageUtilService;
import com.zaatun.zaatunecommerce.utils.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CategoryImageService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UtilService utilService;
    private final ImageUtilService imageUtilService;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<String>> uploadCategoryImage(String categoryId, MultipartFile mpFile, String token, CategoryImageEnum categoryImagePosition) {

        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            MultipartFile[] multipartFiles = new MultipartFile[1];
            multipartFiles[0] = mpFile;
            List<String> imageUrl = imageUtilService.uploadImage(multipartFiles);

//            categoryModel.setCategoryImage(imageUrl.get(0));

            if(categoryImagePosition.equals(CategoryImageEnum.NormalImage)){
                categoryModel.setCategoryImage(imageUrl.get(0));
            }
            else {
                categoryModel.setVerticalImage(imageUrl.get(0));
            }

            categoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
            categoryModel.setUpdateTime(System.currentTimeMillis());


            categoryRepository.save(categoryModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Image Uploaded", imageUrl.get(0)), HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteCategoryImage(String categoryId, String token, CategoryImageEnum categoryImagePosition) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            if(categoryImagePosition.equals(CategoryImageEnum.NormalImage)){
                categoryModel.setCategoryImage(null);
            }
            else {
                categoryModel.setVerticalImage(null);
            }

            categoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
            categoryModel.setUpdateTime(System.currentTimeMillis());

            categoryRepository.save(categoryModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Image Deleted Successful", null), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }

    public ResponseEntity<ApiResponse<String>> uploadSubCategoryImage(String categoryId, MultipartFile mpFile, String token, String subCategoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(subCategoryId);

            if (subCategoryModelOptional.isPresent() && categoryModel.getSubCategories().contains(subCategoryModelOptional.get())) {
                SubCategoryModel subCategoryModel = subCategoryModelOptional.get();

                MultipartFile[] multipartFiles = new MultipartFile[1];
                multipartFiles[0] = mpFile;
                List<String> imageUrl = imageUtilService.uploadImage(multipartFiles);

                subCategoryModel.setSubCategoryImage(imageUrl.get(0));
                subCategoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
                subCategoryModel.setUpdateTime(System.currentTimeMillis());

                subCategoryRepository.save(subCategoryModel);

                return new ResponseEntity<>(new ApiResponse<>(201, "Image Uploaded", imageUrl.get(0)), HttpStatus.CREATED);

            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SubCategory found with this SubCategory Id");

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteSubCategoryImage(String categoryId,String  token, String subCategoryId) {
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(categoryId);

        if (categoryModelOptional.isPresent()) {
            CategoryModel categoryModel = categoryModelOptional.get();

            Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(subCategoryId);

            if (subCategoryModelOptional.isPresent() ) {
                SubCategoryModel subCategoryModel = subCategoryModelOptional.get();

                if(categoryModel.getSubCategories().contains(subCategoryModel)){
                    subCategoryModel.setSubCategoryImage(null);
                    subCategoryModel.setUpdateBy(jwtProvider.getNameFromJwt(token));
                    subCategoryModel.setUpdateTime(System.currentTimeMillis());

                    subCategoryRepository.save(subCategoryModel);

                    return new ResponseEntity<>(new ApiResponse<>(201, "Image Uploaded", subCategoryId), HttpStatus.CREATED);

                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subcategory is not under this category");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SubCategory found with this SubCategory Id");

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category found with this category Id");
        }
    }
}
