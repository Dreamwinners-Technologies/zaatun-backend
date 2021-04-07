package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.AddProductRequest;
import com.zaatun.zaatunecommerce.dto.request.ProductEditRequest;
import com.zaatun.zaatunecommerce.dto.response.ProductResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.CategoryModel;
import com.zaatun.zaatunecommerce.model.ProductModel;
import com.zaatun.zaatunecommerce.model.SubCategoryModel;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import com.zaatun.zaatunecommerce.repository.SubCategoryRepository;
import io.jsonwebtoken.JwtParser;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ProductService {
    private final UtilService utilService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;


    public ResponseEntity<ApiResponse<String>> addProduct(String token, AddProductRequest addProductRequest) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo(addProductRequest.getProductName(), token);
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(addProductRequest.getCategoryId());
        Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(addProductRequest.getSubCategoryId());

        if (categoryModelOptional.isPresent() && subCategoryModelOptional.isPresent()) {

            ProductModel productModel = ProductModel.builder()
                    .productId(basicTableInfo.getId())
                    .createdBy(basicTableInfo.getCreateBy())
                    .createdOn(basicTableInfo.getCreationTime())
                    .productName(addProductRequest.getProductName())
                    .productSlug(basicTableInfo.getSlug())
                    .SKU(basicTableInfo.getSKU())
                    .brand(addProductRequest.getBrand())
                    .categoryModel(categoryModelOptional.get())
                    .subCategoryModel(subCategoryModelOptional.get())
                    .buyingPrice(addProductRequest.getBuyingPrice())
                    .regularPrice(addProductRequest.getRegularPrice())
                    .discountPrice(addProductRequest.getDiscountPrice())
                    .description(addProductRequest.getDescription())
                    .shortDescription(addProductRequest.getShortDescription())
                    .warranty(addProductRequest.getWarranty())
                    .emi(addProductRequest.getEmi())
                    .inStock(addProductRequest.getInStock())
                    .isFeatured(addProductRequest.getIsFeatured())
                    .isAvailable(addProductRequest.getIsAvailable())
                    .videoUrl(addProductRequest.getVideoUrl())
                    .affiliatePercentage(addProductRequest.getAffiliatePercentage())
                    .vat(addProductRequest.getVat())
                    .quantity(utilService.getQuantityModelFromQuantityList(addProductRequest.getQuantity()))
                    .processor(addProductRequest.getProcessor())
                    .battery(addProductRequest.getBattery())
                    .ram(addProductRequest.getRam())
                    .rom(addProductRequest.getRom())
                    .screenSize(addProductRequest.getScreenSize())
                    .backCamera(addProductRequest.getBackCamera())
                    .frontCamera(addProductRequest.getFrontCamera())
                    .build();

            productRepository.save(productModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Product Added Successful", basicTableInfo.getId()), HttpStatus.CREATED);

        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category or SubCategory is Found");

    }

    public ResponseEntity<ApiResponse<ProductResponse>> getProducts(String productName, String brand, String categoryId,
                                                                    String subCategoryId, Boolean inStock, Boolean isFeatured,
                                                                    String sortBy, Sort.Direction orderBy, int pageNo, int pageSize, String productId) {

        ProductModel productModel = ProductModel.builder()
                .productId(productId)
                .productName(productName)
                .brand(brand)
                .categoryModel(CategoryModel.builder().categoryId(categoryId).build())
                .subCategoryModel(SubCategoryModel.builder().subCategoryId(subCategoryId).build())
                .inStock(inStock)
                .isFeatured(isFeatured)
                .build();

        Pageable pageable;
        Sort sort = Sort.by(orderBy, sortBy);

        pageable = PageRequest.of(pageNo, pageSize, sort);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<ProductModel> productModelPage = productRepository.findAll(Example.of(productModel, matcher), pageable);

        ProductResponse productResponse = new ProductResponse(pageSize, pageNo, productModelPage.getContent().size(),
                productModelPage.isLast(), productModelPage.getTotalElements(), productModelPage.getTotalPages(),
                productModelPage.getContent());

        for (ProductModel productModel1 : productModelPage.getContent()) {
            List<SubCategoryModel> subCategoryModels = new ArrayList<>();
            productModel1.getCategoryModel().setSubCategories(subCategoryModels);
        }

        if (productModelPage.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Product Found", productResponse), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Product Found", productResponse), HttpStatus.OK);
        }

    }

    public ResponseEntity<ApiResponse<String>> editProduct(String token, @Valid ProductEditRequest productEditRequest, String productId) {
        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        if (productModelOptional.isPresent()) {
            ProductModel productModel = productModelOptional.get();
            BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

            Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(productEditRequest.getCategoryId());
            Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(productEditRequest.getSubCategoryId());

            System.out.println(productModel.getProductId());
            System.out.println(productModel.getProductName());
            System.out.println(productModel.getRam());

            if (categoryModelOptional.isPresent() && subCategoryModelOptional.isPresent()) {

                productModel.setUpdatedBy(basicTableInfo.getCreateBy());
                productModel.setUpdatedOn(basicTableInfo.getCreationTime());
                productModel.setProductName(productEditRequest.getProductName());
                productModel.setBrand(productEditRequest.getBrand());
                productModel.setCategoryModel(categoryModelOptional.get());
                productModel.setSubCategoryModel(subCategoryModelOptional.get());
                productModel.setBuyingPrice(productEditRequest.getBuyingPrice());
                productModel.setRegularPrice(productEditRequest.getRegularPrice());
                productModel.setDiscountPrice(productEditRequest.getDiscountPrice());
                productModel.setDescription(productEditRequest.getDescription());
                productModel.setShortDescription(productEditRequest.getShortDescription());
                productModel.setWarranty(productEditRequest.getEmi());
                productModel.setInStock(productEditRequest.getInStock());
                productModel.setIsFeatured(productEditRequest.getIsFeatured());
                productModel.setIsAvailable(productEditRequest.getIsAvailable());
                productModel.setVideoUrl(productEditRequest.getVideoUrl());
                productModel.setAffiliatePercentage(productEditRequest.getAffiliatePercentage());
                productModel.setVat(productEditRequest.getVat());
                productModel.setQuantity(utilService.getQuantityModelFromQuantityList(productEditRequest.getQuantity()));
                productModel.setProcessor(productEditRequest.getProcessor());
                productModel.setBattery(productEditRequest.getBattery());
                productModel.setRam(productEditRequest.getRam());
                productModel.setRom(productEditRequest.getRom());
                productModel.setScreenSize(productEditRequest.getScreenSize());
                productModel.setBackCamera(productEditRequest.getBackCamera());
                productModel.setFrontCamera(productEditRequest.getFrontCamera());

                productRepository.save(productModel);

                return new ResponseEntity<>(new ApiResponse<>(201, "Product Edit Successful", productModel.getProductId()), HttpStatus.CREATED);

            }

        }
        return null;
    }

    public ResponseEntity<ApiResponse<String>> deleteProduct(String productId) {
        productRepository.deleteById(productId);

        return new ResponseEntity<>(new ApiResponse<>(201, "Product Delete Successful", productId), HttpStatus.CREATED);

    }
}