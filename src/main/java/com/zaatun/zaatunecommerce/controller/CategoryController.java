package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.CategoryAddEditRequest;
import com.zaatun.zaatunecommerce.dto.request.CategoryImageEnum;
import com.zaatun.zaatunecommerce.dto.request.SubCategoryAddEditRequest;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.service.CategoryImageService;
import com.zaatun.zaatunecommerce.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/categories/")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryImageService categoryImageService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCategory(@RequestHeader(name = "Authorization") String token,
                                                   @RequestBody CategoryAddEditRequest categoryAddEditRequest){
        return categoryService.addCategory(token, categoryAddEditRequest);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> editCategory(@RequestHeader(name = "Authorization") String token,
                                                            @RequestBody CategoryAddEditRequest categoryAddEditRequest,
                                                            @PathVariable String categoryId){
        return categoryService.editCategory(token, categoryAddEditRequest, categoryId);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable String categoryId){
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryModel>>> getCategoriesList(){
        return categoryService.getCategoriesList();
    }



    @PostMapping("{categoryId}/subCategories")
    public ResponseEntity<ApiResponse<String>> addSubCategory(@RequestHeader(name = "Authorization") String token,
                                                              @RequestBody SubCategoryAddEditRequest subCategoryAddEditRequest,
                                                              @PathVariable String categoryId){
        return categoryService.addSubCategory(token, subCategoryAddEditRequest, categoryId);
    }

    @PutMapping("{categoryId}/subCategories/{subCategoryId}")
    public ResponseEntity<ApiResponse<String>> editSubCategory(@RequestHeader(name = "Authorization") String token,
                                                               @RequestBody SubCategoryAddEditRequest subCategoryAddEditRequest,
                                                               @PathVariable String categoryId, @PathVariable String subCategoryId){
        return categoryService.editSubCategory(token, subCategoryAddEditRequest, categoryId, subCategoryId);
    }

    @DeleteMapping("{categoryId}/subCategories/{subCategoryId}")
    public ResponseEntity<ApiResponse<String>> deleteSubCategory(@RequestHeader(name = "Authorization") String token,
                                                               @PathVariable String categoryId, @PathVariable String subCategoryId){
        return categoryService.deleteSubCategory(token, categoryId, subCategoryId);
    }

    @PostMapping("{categoryId}/subCategories/image/{subCategoryId}")
    public ResponseEntity<ApiResponse<String>> addSubCategoryImage(@RequestHeader(name = "Authorization") String token,
                                                                   @PathVariable String categoryId, MultipartFile mpFile, @
                                                                               PathVariable String subCategoryId){
        return categoryImageService.uploadSubCategoryImage(categoryId, mpFile, token, subCategoryId);
    }

    @DeleteMapping("{categoryId}/subCategories/image/{subCategoryId}")
    public ResponseEntity<ApiResponse<String>> deleteSubCategoryImage(@RequestHeader(name = "Authorization") String token,
                                                                      @PathVariable String categoryId,
                                                                      @PathVariable String subCategoryId){
        return categoryImageService.deleteSubCategoryImage(categoryId, token, subCategoryId);
    }

    @PostMapping("image/{categoryId}")
    public ResponseEntity<ApiResponse<String>> addCategoryImage(@RequestHeader(name = "Authorization") String token,
                                                                @PathVariable String categoryId, MultipartFile mpFile,
                                                                @RequestParam CategoryImageEnum categoryImagePosition){

        return categoryImageService.uploadCategoryImage(categoryId, mpFile, token, categoryImagePosition);
    }

    @DeleteMapping("image/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategoryImage(@RequestHeader(name = "Authorization") String token,
                                                                   @PathVariable String categoryId,
                                                                   @RequestParam CategoryImageEnum categoryImagePosition){
        return categoryImageService.deleteCategoryImage(categoryId, token, categoryImagePosition);
    }

}
