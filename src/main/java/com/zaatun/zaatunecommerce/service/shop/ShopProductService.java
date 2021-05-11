package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.AddReviewRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.AffiliateUserRepository;
import com.zaatun.zaatunecommerce.repository.AffiliateUserTrackerRepository;
import com.zaatun.zaatunecommerce.repository.ProductRepository;
import com.zaatun.zaatunecommerce.repository.ProfileRepository;
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
    private final JwtProvider jwtProvider;
    private final ProfileRepository profileRepository;

    //Get All products with filtering options for Store
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponse>>>>
    getProducts(String productName, String brand, String categorySlug, String subCategorySlug,
                String productSlug, Boolean inStock, Boolean isFeatured, String processor,
                String battery, String ram, String rom, String screenSize, String backCamera,
                String frontCamera, String sortBy, Sort.Direction orderBy, int pageSize,
                int pageNo, Integer rating, String token) {

        String userId = getUserId(token);

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
                .categoryModel(CategoryModel.builder().categorySlug(categorySlug).build())
                .subCategoryModel(SubCategoryModel.builder().subCategoryId(subCategorySlug).build())
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
        List<ShopProductResponse> shopProductResponses =
                shopProductHelperService.shopProductResponseFromProductsDb(productModels, userId);

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


    public ResponseEntity<ApiResponse<ShopProductResponse>> getProductBySlug(String productSlug, String affiliateUserSlug, String token) {
        Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(productSlug);

        String userId = getUserId(token);

        if (productModelOptional.isPresent()) {
            ProductModel productModel = productModelOptional.get();

            ShopProductResponse shopProductResponse =
                    shopProductHelperService.migrateProductModelToShopProductResponse(productModel, userId);

            if (affiliateUserSlug != null && !affiliateUserSlug.isEmpty()) {
                Optional<AffiliateUserModel> affiliateUserModelOptional = affiliateUserRepository.findByAffiliateUserSlug(affiliateUserSlug);
                if (affiliateUserModelOptional.isPresent()) {
                    AffiliateUserModel affiliateUserModel = affiliateUserModelOptional.get();

                    if (affiliateUserModel.getProfileModel().getIsAffiliate()) {
                        String referralId = UUID.randomUUID().toString();
                        AffiliateUserTrackerModel affiliateUserTrackerModel = new AffiliateUserTrackerModel(referralId,
                                productModel.getProductSlug(), affiliateUserModel);

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

    private String getUserId(String token) {
        String userId = null;
        String username = jwtProvider.getUserNameFromJwt(token);
        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);
        if(profileModelOptional.isPresent()){
            userId = profileModelOptional.get().getId();
        }
        return userId;
    }

    public ResponseEntity<ApiResponse<String>> addReview(String token, String productSlug, AddReviewRequest addReviewRequest) {
        String username = jwtProvider.getUserNameFromJwt(token);

        Optional<ProfileModel> profileModelOptional = profileRepository.findByUsername(username);

        if (profileModelOptional.isPresent()) {
            ProfileModel profileModel = profileModelOptional.get();

            Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(productSlug);
            if (productModelOptional.isPresent()) {
                ProductModel productModel = productModelOptional.get();


                if (!productModel.getBuyersId().contains(profileModel.getId())) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You Are Not Permitted to Give Review. First Buy The Product.");
                }

                List<ProductReviewModel> productReviewModels = productModel.getProductReviews();
                ProductReviewModel productReviewModel = new ProductReviewModel(0L, System.currentTimeMillis(), username,
                        profileModel.getName(), addReviewRequest.getReviewStar(), addReviewRequest.getComment(), productModel);
                productReviewModels.add(productReviewModel);

                productRepository.save(productModel);

                return new ResponseEntity<>(new ApiResponse<>(201, "Comment Created", null), HttpStatus.CREATED);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product Found with that id.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No User Found");
        }
    }

    public ResponseEntity<ApiResponse<List<ProductReviewModel>>> getReviewList(String productSlug) {
        Optional<ProductModel> productModelOptional = productRepository.findByProductSlug(productSlug);
        if (productModelOptional.isPresent()) {
            ProductModel productModel = productModelOptional.get();

            List<ProductReviewModel> productReviewModels = productModel.getProductReviews();

            return new ResponseEntity<>(new ApiResponse<>(200, "Review Found", productReviewModels), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Product Found with that id.");
        }

    }
}



