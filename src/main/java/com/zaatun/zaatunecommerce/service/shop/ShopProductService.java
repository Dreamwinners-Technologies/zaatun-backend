package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.AffiliateUserRepository;
import com.zaatun.zaatunecommerce.repository.AffiliateUserTrackerRepository;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
// Store Product logics
public class ShopProductService {
    //Data access repositories
    private final ProductRepository productRepository;
    private final ShopProductHelperService shopProductHelperService;
    private final AffiliateUserRepository affiliateUserRepository;
    private final AffiliateUserTrackerRepository affiliateUserTrackerRepository;

    //Get All products with filtering options for Store
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponse>>>>
    getProducts(String productName, String brand, String categoryId, String subCategoryId,
                String productSlug, Boolean inStock, Boolean isFeatured, String processor,
                String battery, String ram, String rom, String screenSize, String backCamera,
                String frontCamera, String sortBy, Sort.Direction orderBy, int pageSize,
                int pageNo, Integer rating) {

        //Example Specification for filtering
        SpecificationModel exSpecification = SpecificationModel.builder()
                .processor(processor)
                .battery(battery)
                .ram(ram)
                .rom(rom)
                .screenSize(screenSize)
                .backCamera(backCamera)
                .frontCamera(frontCamera)
                .build();

        //Example Product for filtering
        ProductModel exProduct = ProductModel.builder()
                .productSlug(productSlug)
                .productName(productName)
                .brand(brand)
                .categoryModel(CategoryModel.builder().categoryId(categoryId).build())
                .subCategoryModel(SubCategoryModel.builder().subCategoryId(subCategoryId).build())
                .inStock(inStock)
                .isFeatured(isFeatured)
                .specification(exSpecification)
                .build();

        //Sort Functionality
        Pageable pageable;
        Sort sort = Sort.by(orderBy, sortBy); //OrderBy is Column name and sortBy is Direction

        pageable = PageRequest.of(pageNo, pageSize, sort); //Make pageable object for pagination

        //Example matcher logics for advance searching
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()) //productName advance search
                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        //Getting products from database with pagination
        Page<ProductModel> productModelPage = productRepository.findAll(Example.of(exProduct, matcher), pageable);

        //Getting only the products
        List<ProductModel> productModels = productModelPage.getContent();

        //Migration from ProductModel List to ShopProductResponse List for Store.
        List<ShopProductResponse> shopProductResponses = shopProductHelperService.shopProductResponseFromProductsDb(productModels);

        //Setting custom pagination info with the list of productResponse list
        PaginationResponse<List<ShopProductResponse>> paginationResponse = new PaginationResponse<>(pageSize, pageNo,
                shopProductResponses.size(), productModelPage.isLast(), productModelPage.getTotalElements(),
                productModelPage.getTotalPages(), shopProductResponses);

        if (productModelPage.isEmpty()) {
            //If there is no product found
            return new ResponseEntity<>(new ApiResponse<>(200, "No Product Found", paginationResponse), HttpStatus.OK);
        } else {
            //If products are found
            return new ResponseEntity<>(new ApiResponse<>(200, "Product Found", paginationResponse), HttpStatus.OK);
        }
    }


    public ResponseEntity<ApiResponse<ShopProductResponse>> getProductBySlug(String productSlug, String affiliateUserSlug) {
        Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(productSlug);

        if (productModelOptional.isPresent()) {
            ProductModel productModel = productModelOptional.get();

            ShopProductResponse shopProductResponse = shopProductHelperService.migrateProductModelToShopProductResponse(productModel);

            if (affiliateUserSlug != null && !affiliateUserSlug.isEmpty()) {
                Optional<AffiliateUserModel> affiliateUserModelOptional = affiliateUserRepository.findByAffiliateUserSlug(affiliateUserSlug);
                if (affiliateUserModelOptional.isPresent()) {
                    AffiliateUserModel affiliateUserModel = affiliateUserModelOptional.get();

                    if(affiliateUserModel.getProfileModel().getIsAffiliate()){
                        String referralId = UUID.randomUUID().toString();
                        AffiliateUserTrackerModel affiliateUserTrackerModel = new AffiliateUserTrackerModel(referralId,
                                productModel.getProductSlug(), affiliateUserModel.getAffiliateUserSlug());

                        affiliateUserTrackerRepository.save(affiliateUserTrackerModel);
                        shopProductResponse.setReferralId(referralId);
                    }

                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Affiliate User found with this Id: " + affiliateUserSlug);
                }
            }

            return new ResponseEntity<>(new ApiResponse<>(200, "Product Found", shopProductResponse), HttpStatus.OK);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product found with slug: " + productSlug);
        }
    }
}

