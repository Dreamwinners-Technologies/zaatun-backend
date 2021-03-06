package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AddAttributesRequest;
import com.zaatun.zaatunecommerce.dto.request.AddProductRequest;
import com.zaatun.zaatunecommerce.dto.request.DeleteImageRequest;
import com.zaatun.zaatunecommerce.dto.request.ProductEditRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.ProductResponse;
import com.zaatun.zaatunecommerce.model.ProductAttributesModel;
import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import com.zaatun.zaatunecommerce.service.ProductReviewService;
import com.zaatun.zaatunecommerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/products")
public class ProductController {
    private final ProductService productService;
    private final ProductReviewService productReviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addProduct(@RequestHeader(name = "Authorization") String token,
                                                          @Valid @RequestBody AddProductRequest addProductRequest){
        return productService.addProduct(token, addProductRequest);
    }

    @PostMapping("/productAttributes")
    public ResponseEntity<ApiResponse<ProductAttributesModel>> addProductAttributes(@RequestHeader(name = "Authorization") String token,
                                                                                    @Valid @RequestBody AddAttributesRequest addAttributesRequest){
        return productService.addProductAttributes(token, addAttributesRequest);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponse>> getProducts(@RequestParam(required = false) String productName, String brand,
                                                                    String categoryId, String subCategoryId, String productId,
                                                                    Boolean inStock,
                                                                    Boolean isFeatured,
                                                                    @RequestParam(defaultValue = "createdOn") String sortBy,
                                                                    @RequestParam(defaultValue = "ASC") Sort.Direction orderBy,
                                                                    @RequestParam(defaultValue = "20") int pageSize,
                                                                    @RequestParam(defaultValue = "0") int pageNo ){

        return productService.getProducts(productName, brand, categoryId, subCategoryId, inStock,
                isFeatured, sortBy, orderBy, pageNo, pageSize, productId);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> editProduct(@RequestHeader(name = "Authorization") String token,
                                      @Valid @RequestBody ProductEditRequest productEditRequest,
                                      @PathVariable String productId){
        return productService.editProduct(token, productEditRequest, productId);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable String productId){
        return productService.deleteProduct( productId);
    }

    @PostMapping("/image/{productId}")
    public ResponseEntity<ApiResponse<List<String>>> addProductImages(@RequestHeader(name = "Authorization") String token,
                                                                      @PathVariable String productId,
                                                                      MultipartFile[] mpFiles){
        return productService.addProductImages(token, productId, mpFiles);
    }

    @DeleteMapping("/image/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProductImages(@RequestHeader(name = "Authorization") String token,
                                                                         @PathVariable String productId,
                                                                         @RequestBody DeleteImageRequest deleteImageRequest){
        return productService.deleteProductImages(token, productId, deleteImageRequest);
    }

    @GetMapping("/review")
    public ResponseEntity<ApiResponse<PaginationResponse<List<ProductReviewModel>>>>  getAllReviews(@RequestParam(defaultValue = "50") int pageSize,
                                                                                                    @RequestParam(defaultValue = "0") int pageNo,
                                                                                                    @RequestParam(required = false) String productSlug,
                                                                                                    @RequestParam(required = false) String reviewBy){
        return productReviewService.getAllReview(pageSize, pageNo, productSlug, reviewBy);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Long reviewId){
        return productReviewService.deleteReview(reviewId);
    }
}

// Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJaVE4tVS0wMTUxNTIxMjY4NyIsIm5hbWUiOiJIYWJpYiIsInNjb3BlcyI6IlVTRVIiLCJpYXQiOjE2MjAxNTU0NzEsImV4cCI6MTYyMjI1NTQ3MX0.7rXza8GFfVgYGnVoGe9MHtVDQasPEOKMF31-m_k3cZ7gHiez_nS5q_rM_vEFo-Z_kjKfL49jJymaFwWtvREQGQ


//8b87d080-9441-4045-8d6f-b15f41de4ebb

