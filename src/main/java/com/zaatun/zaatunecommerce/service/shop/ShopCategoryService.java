package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopCategoryResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopSubCategoryResponse;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ShopCategoryService {
    private final CategoryRepository categoryRepository;

    public ResponseEntity<ApiResponse<List<ShopCategoryResponse>>> getCategories() {
        List<CategoryModel> categoryModelList = categoryRepository.findAll();

        List<ShopCategoryResponse> shopCategoryResponseList = new ArrayList<>();
        if(categoryModelList.isEmpty()){
            return new ResponseEntity<>(new ApiResponse<>(200, "No Category List Found", shopCategoryResponseList), HttpStatus.OK);
        }
        else {

            for (CategoryModel categoryModel: categoryModelList){
                List<ShopSubCategoryResponse> shopSubCategoryResponses = new ArrayList<>();
                for (SubCategoryModel subCategoryModel: categoryModel.getSubCategories()){
                    ShopSubCategoryResponse shopSubCategoryResponse = new ShopSubCategoryResponse(
                            subCategoryModel.getSubCategoryName(), subCategoryModel.getSubCategoryIcon(),
                            subCategoryModel.getSubCategorySlug(), subCategoryModel.getSubCategoryImage());

                    shopSubCategoryResponses.add(shopSubCategoryResponse);
                }

                ShopCategoryResponse shopCategoryResponse = new ShopCategoryResponse(categoryModel.getCategoryName(),
                        categoryModel.getCategoryIcon(), categoryModel.getCategorySlug(), categoryModel.getCategoryImage(),
                        shopSubCategoryResponses);

                shopCategoryResponseList.add(shopCategoryResponse);
            }

            return new ResponseEntity<>(new ApiResponse<>(200, "Category List Found", shopCategoryResponseList), HttpStatus.OK);
        }
    }
}
