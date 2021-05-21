package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopCategoryResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopSubCategoryResponse;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.ProductModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import liquibase.pro.packaged.S;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class ShopCategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
                        categoryModel.getVerticalImage(), shopSubCategoryResponses);

                shopCategoryResponseList.add(shopCategoryResponse);
            }

            return new ResponseEntity<>(new ApiResponse<>(200, "Category List Found", shopCategoryResponseList), HttpStatus.OK);
        }
    }

    public ResponseEntity<ApiResponse<List<String>>> getBrands() {

        List<ProductModel> productModels = productRepository.findAll();

        Set<String> brands = new HashSet<>();
        for (ProductModel productModel: productModels){
            brands.add(productModel.getBrand().toLowerCase());
        }

        List<String> brandList = new ArrayList<>(brands);

        brandList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        return new ResponseEntity<>(new ApiResponse<>(200,"Brands Found",brandList), HttpStatus.OK);
    }
}
