package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.*;
import com.zaatun.zaatunecommerce.dto.response.ProductResponse;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.CategoryRepository;
import com.zaatun.zaatunecommerce.repository.ProductAttributesModelRepository;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import com.zaatun.zaatunecommerce.repository.SubCategoryRepository;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final ImageUtilService imageUtilService;
    private final ProductAttributesModelRepository productAttributesModelRepository;


    public ResponseEntity<ApiResponse<String>> addProduct(String token, AddProductRequest addProductRequest) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo(addProductRequest.getProductName(), token);
        Optional<CategoryModel> categoryModelOptional = categoryRepository.findById(addProductRequest.getCategoryId());
        Optional<SubCategoryModel> subCategoryModelOptional = subCategoryRepository.findById(addProductRequest.getSubCategoryId());

        if (categoryModelOptional.isPresent() && subCategoryModelOptional.isPresent()) {

            AddSpecificationRequest addSpecification = addProductRequest.getAddSpecification();

            List<DynamicSpecificationModel> dynamicSpecificationModels = new ArrayList<>();
            for (AddDynamicSpecificationRequest dynamicSpecification : addSpecification.getDynamicSpecifications()) {
                DynamicSpecificationModel dynamicSpecificationModel = new DynamicSpecificationModel();
                dynamicSpecificationModel.setKey(dynamicSpecification.getKey());
                dynamicSpecificationModel.setValue(dynamicSpecification.getValue());

                dynamicSpecificationModels.add(dynamicSpecificationModel);
            }

            SpecificationModel specificationModel = SpecificationModel.builder()
                    .processor(addSpecification.getProcessor())
                    .battery(addSpecification.getBattery())
                    .ram(addSpecification.getRam())
                    .rom(addSpecification.getRom())
                    .operatingSystem(addSpecification.getOperatingSystem())
                    .screenSize(addSpecification.getScreenSize())
                    .backCamera(addSpecification.getBackCamera())
                    .frontCamera(addSpecification.getFrontCamera())
                    .dynamicSpecifications(dynamicSpecificationModels)
                    .build();

//            List<String> imageLinks = new ArrayList<>();


            ProductModel productModel = ProductModel.builder()
                    .productId(basicTableInfo.getId())
                    .createdBy(basicTableInfo.getCreateBy())
                    .createdOn(basicTableInfo.getCreationTime())
                    .productName(addProductRequest.getProductName())
                    .productSlug(basicTableInfo.getSlug())
                    .productBadge(addProductRequest.getProductBadge())
                    .SKU(basicTableInfo.getSKU())
                    .brand(addProductRequest.getBrand())
                    .categoryModel(categoryModelOptional.get())
                    .subCategoryModel(subCategoryModelOptional.get())
                    .description(addProductRequest.getDescription())
                    .shortDescription(addProductRequest.getShortDescription())
                    .warranty(addProductRequest.getWarranty())
                    .emi(addProductRequest.getEmi())
                    .inStock(addProductRequest.getInStock())
                    .isFeatured(addProductRequest.getIsFeatured())
                    .isDiscount(addProductRequest.getIsDiscount())
                    .videoUrl(addProductRequest.getVideoUrl())
                    .affiliatePercentage(addProductRequest.getAffiliatePercentage())
                    .vat(addProductRequest.getVat())
                    .totalSold(0L)
                    .specification(specificationModel)
                    .productAttributeModels(addProductRequest.getProductAttributeModels())
//                    .productImages(imageLinks)
                    .build();


            List<ProductVariationModel> productVariationModels = new ArrayList<>();
            for (AddVariationRequest addVariationRequest: addProductRequest.getVariations()){
                ProductVariationModel productVariationModel = new ProductVariationModel(0L, addVariationRequest.getStock(),
                        addVariationRequest.getInStock(), addVariationRequest.getIsDefault(), addVariationRequest.getBuyingPrice(),
                        addVariationRequest.getRegularPrice(), addVariationRequest.getDiscountPrice(),
                        addVariationRequest.getAttributeCombinations(), productModel);
                productVariationModels.add(productVariationModel);
            }

            productModel.setVariations(productVariationModels);

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


            if (categoryModelOptional.isPresent() && subCategoryModelOptional.isPresent()) {

                AddSpecificationRequest addSpecification = productEditRequest.getAddSpecification();

                List<DynamicSpecificationModel> dynamicSpecificationModels = new ArrayList<>();
                for (AddDynamicSpecificationRequest dynamicSpecification : addSpecification.getDynamicSpecifications()) {
                    DynamicSpecificationModel dynamicSpecificationModel = new DynamicSpecificationModel();
                    dynamicSpecificationModel.setKey(dynamicSpecification.getKey());
                    dynamicSpecificationModel.setValue(dynamicSpecification.getValue());

                    dynamicSpecificationModels.add(dynamicSpecificationModel);
                }

                SpecificationModel specificationModel = productModel.getSpecification();
                specificationModel.setProcessor(addSpecification.getProcessor());
                specificationModel.setBattery(addSpecification.getBattery());
                specificationModel.setRam(addSpecification.getRam());
                specificationModel.setRom(addSpecification.getRom());
                specificationModel.setOperatingSystem(addSpecification.getOperatingSystem());
                specificationModel.setScreenSize(addSpecification.getScreenSize());
                specificationModel.setBackCamera(addSpecification.getBackCamera());
                specificationModel.setFrontCamera(addSpecification.getFrontCamera());
                specificationModel.setDynamicSpecifications(dynamicSpecificationModels);

                productModel.setUpdatedBy(basicTableInfo.getCreateBy());
                productModel.setUpdatedOn(basicTableInfo.getCreationTime());
                productModel.setProductName(productEditRequest.getProductName());
                productModel.setBrand(productEditRequest.getBrand());
                productModel.setProductBadge(productEditRequest.getProductBadge());
                productModel.setCategoryModel(categoryModelOptional.get());
                productModel.setSubCategoryModel(subCategoryModelOptional.get());
                productModel.setDescription(productEditRequest.getDescription());
                productModel.setShortDescription(productEditRequest.getShortDescription());
                productModel.setWarranty(productEditRequest.getEmi());
                productModel.setInStock(productEditRequest.getInStock());
                productModel.setIsFeatured(productEditRequest.getIsFeatured());
                productModel.setIsDiscount(productEditRequest.getIsDiscount());
                productModel.setVideoUrl(productEditRequest.getVideoUrl());
                productModel.setAffiliatePercentage(productEditRequest.getAffiliatePercentage());
                productModel.setVat(productEditRequest.getVat());
                productModel.setSpecification(specificationModel);
                productModel.setDeliveryInfo(productEditRequest.getDeliveryInfo());
                productModel.setKey(productEditRequest.getKey());
                productModel.setValue(productEditRequest.getValue());

                productModel.setProductAttributeModels(productEditRequest.getProductAttributeModels());

                List<ProductVariationModel> productVariationModels = new ArrayList<>();
                for (ProductVariationModel productVariationModel: productEditRequest.getVariations()){
                    if(productVariationModel.getId() != null){
                        productVariationModels.add(productVariationModel);
                    }
                    else {
                        ProductVariationModel newProductVariationModel = new ProductVariationModel(0L, productVariationModel.getStock(),
                                productVariationModel.getInStock(), productVariationModel.getIsDefault(),
                                productVariationModel.getBuyingPrice(), productVariationModel.getRegularPrice(),
                                productVariationModel.getDiscountPrice(), productVariationModel.getAttributeCombinations(), productModel);

                        productVariationModels.add(newProductVariationModel);
                    }

                }

                productModel.setVariations(productVariationModels);

                productRepository.save(productModel);

                return new ResponseEntity<>(new ApiResponse<>(201, "Product Edit Successful", productModel.getProductId()), HttpStatus.CREATED);

            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product found with is id: " + productId);

        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Category or SubCategory is Found");
    }

    public ResponseEntity<ApiResponse<String>> deleteProduct(String productId) {
        productRepository.deleteById(productId);

        return new ResponseEntity<>(new ApiResponse<>(201, "Product Delete Successful", productId), HttpStatus.CREATED);

    }

    public ResponseEntity<ApiResponse<List<String>>> addProductImages(String token, String productId, MultipartFile[] mpFiles) {


        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        if (productModelOptional.isPresent()) {
            List<String> imageLinks = imageUtilService.uploadImage(mpFiles);

            BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

            ProductModel productModel = productModelOptional.get();

            productModel.setUpdatedBy(basicTableInfo.getCreateBy());
            productModel.setUpdatedOn(basicTableInfo.getCreationTime());

            List<String> productImageLinks = productModel.getProductImages();


            productImageLinks.addAll(imageLinks);

            productRepository.save(productModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Image Uploaded", imageLinks), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product Found");
    }

    public ResponseEntity<ApiResponse<String>> deleteProductImages(String token, String productId,
                                                                         DeleteImageRequest deleteImageRequest) {
        Optional<ProductModel> productModelOptional = productRepository.findById(productId);

        if (productModelOptional.isPresent()) {
            BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

            ProductModel productModel = productModelOptional.get();

            productModel.setUpdatedBy(basicTableInfo.getCreateBy());
            productModel.setUpdatedOn(basicTableInfo.getCreationTime());

            List<String> imageLinks = productModel.getProductImages();

            if(imageLinks.contains(deleteImageRequest.getImageLink())){
                imageLinks.remove(deleteImageRequest.getImageLink());
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image link is not matched with product");
            }

            productRepository.save(productModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Image Deleted", null), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product Found");
    }

    public ResponseEntity<ApiResponse<ProductAttributesModel>> addProductAttributes(String token, AddAttributesRequest addAttributesRequest) {
        String id = UUID.randomUUID().toString();

        List<ProductAttribute> productAttributeList = new ArrayList<>();

        for (String attribute: addAttributesRequest.getValues()){
            ProductAttribute productAttribute = new ProductAttribute(0L, attribute);
            productAttributeList.add(productAttribute);
        }

        ProductAttributesModel productAttributesModel = new ProductAttributesModel(id, addAttributesRequest.getAttributeName(),
                productAttributeList);

        productAttributesModel = productAttributesModelRepository.save(productAttributesModel);

        return new ResponseEntity<>(new ApiResponse<>(201, "Attributes Created", productAttributesModel), HttpStatus.CREATED);
    }
}
