package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.ProductResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopCategoryResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopSubCategoryResponse;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.ProductModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ShopProductService {
    private final ProductRepository productRepository;


    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponse>>>> getProducts(String productName, String brand, String categoryId, String subCategoryId,
                                                                              String productSlug, Boolean inStock, Boolean isFeatured, String processor,
                                                                              String battery, String ram, String rom, String screenSize, String backCamera,
                                                                              String frontCamera, String sortBy, Sort.Direction orderBy, int pageSize,
                                                                              int pageNo, Integer rating) {

        ProductModel exProduct = ProductModel.builder()
                .productSlug(productSlug)
                .productName(productName)
                .brand(brand)
                .categoryModel(CategoryModel.builder().categoryId(categoryId).build())
                .subCategoryModel(SubCategoryModel.builder().subCategoryId(subCategoryId).build())
                .inStock(inStock)
                .isFeatured(isFeatured)
                .processor(processor)
                .battery(battery)
                .ram(ram)
                .rom(rom)
                .screenSize(screenSize)
                .backCamera(backCamera)
                .frontCamera(frontCamera)
                .build();

        Pageable pageable;
        Sort sort = Sort.by(orderBy, sortBy);

        pageable = PageRequest.of(pageNo, pageSize, sort);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<ProductModel> productModelPage = productRepository.findAll(Example.of(exProduct, matcher), pageable);

        List<ProductModel> productModels = productModelPage.getContent();

        List<ShopProductResponse> shopProductResponses = new ArrayList<>();
        for (ProductModel productModel : productModels) {

            CategoryModel categoryModel = productModel.getCategoryModel();
            List<ShopSubCategoryResponse> shopSubCategoryResponses = new ArrayList<>();
            ShopCategoryResponse shopCategoryResponse = new ShopCategoryResponse(categoryModel.getCategoryName(),
                    categoryModel.getCategoryIcon(), categoryModel.getCategorySlug(), categoryModel.getCategoryImage(),
                    shopSubCategoryResponses);

            SubCategoryModel subCategoryModel = productModel.getSubCategoryModel();
            ShopSubCategoryResponse shopSubCategoryResponse = new ShopSubCategoryResponse(
                    subCategoryModel.getSubCategoryName(), subCategoryModel.getSubCategoryIcon(),
                    subCategoryModel.getSubCategorySlug(), subCategoryModel.getSubCategoryImage());

            ShopProductResponse shopProductResponse = new ShopProductResponse(productModel.getProductName(),
                    productModel.getProductSlug(), productModel.getSKU(), productModel.getBrand(), shopCategoryResponse,
                    shopSubCategoryResponse, productModel.getRegularPrice(), productModel.getDiscountPrice(),
                    productModel.getDescription(), productModel.getShortDescription(), productModel.getWarranty(),
                    productModel.getEmi(), productModel.getInStock(), productModel.getIsFeatured(),
                    productModel.getIsAvailable(), productModel.getVideoUrl(), productModel.getAffiliatePercentage(),
                    productModel.getVat(), productModel.getProductImages(), productModel.getQuantity(),
                    productModel.getProductReviews(), productModel.getProcessor(), productModel.getBattery(),
                    productModel.getRam(), productModel.getRom(), productModel.getScreenSize(),
                    productModel.getBackCamera(), productModel.getFrontCamera());

            shopProductResponses.add(shopProductResponse);
        }

        PaginationResponse<List<ShopProductResponse>> paginationResponse = new PaginationResponse<>(pageSize, pageNo,
                shopProductResponses.size(), productModelPage.isLast(), productModelPage.getTotalElements(),
                productModelPage.getTotalPages(), shopProductResponses);

        if (productModelPage.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Product Found", paginationResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Product Found", paginationResponse), HttpStatus.OK);
        }
    }

}

