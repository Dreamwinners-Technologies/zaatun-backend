package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.AddReviewRequest;
import com.zaatun.zaatunecommerce.dto.request.shop.ProductSortBy;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponseV2;
import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import com.zaatun.zaatunecommerce.service.shop.ShopProductService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store/products")
public class ShopProductController {
    private final ShopProductService shopProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponse>>>> getProducts(
            @RequestParam(required = false) String productName, String brand,
            String categorySlug, String subCategorySlug, String productSlug,
            Boolean inStock, Boolean isFeatured, String processor,
            String battery, String ram,
            String rom, String screenSize, String backCamera, String frontCamera,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction orderBy,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) Integer rating,
            @RequestHeader(name = "Authorization", required = false) String token) {

        return shopProductService.getProducts(productName, brand, categorySlug, subCategorySlug, productSlug, inStock,
                isFeatured, processor, battery, ram, rom, screenSize, backCamera, frontCamera, sortBy, orderBy,
                pageSize, pageNo, rating, token);
    }

    @GetMapping("/v2/")
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponseV2>>>> getProductsV2(
            @RequestParam(required = false) String productName, String brand,
            String categorySlug, String subCategorySlug, String productSlug,
            Boolean inStock, Boolean isFeatured, String processor,
            String battery, String ram,
            String rom, String screenSize, String backCamera, String frontCamera,
            @RequestParam(defaultValue = "createdOn") ProductSortBy sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction orderBy,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) Integer rating,
            @RequestHeader(name = "Authorization", required = false) String token) {

        return shopProductService.getProductsV2(productName, brand, categorySlug, subCategorySlug, productSlug, inStock,
                isFeatured, processor, battery, ram, rom, screenSize, backCamera, frontCamera, sortBy, orderBy,
                pageSize, pageNo, rating, token);
    }

    @GetMapping("/{productSlug}")
    public ResponseEntity<ApiResponse<ShopProductResponse>> getProductBySlug(
            @PathVariable String productSlug,
            @RequestParam(required = false) String affiliateUserSlug,
            @RequestHeader(name = "Authorization", required = false) String token) {

        return shopProductService.getProductBySlug(productSlug, affiliateUserSlug, token);
    }

    @GetMapping("/{productSlug}/details")
    public ResponseEntity<ApiResponse<String>> getProductDetailsBySlug(@PathVariable String productSlug) {

        return shopProductService.getProductDetailsBySlug(productSlug);
    }

    @PostMapping("/{productSlug}/review")
    public ResponseEntity<ApiResponse<String>> addReview(@RequestHeader(name = "Authorization") String token,
                                                         @PathVariable String productSlug,
                                                         @RequestBody AddReviewRequest addReviewRequest) {
        return shopProductService.addReview(token, productSlug, addReviewRequest);
    }

    @GetMapping("/{productSlug}/review")
    public ResponseEntity<ApiResponse<List<ProductReviewModel>>> getReviewList(@PathVariable String productSlug) {
        return shopProductService.getReviewList(productSlug);
    }

}
