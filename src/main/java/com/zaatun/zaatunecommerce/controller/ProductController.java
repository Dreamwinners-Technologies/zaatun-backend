package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AddProductRequest;
import com.zaatun.zaatunecommerce.dto.request.ProductEditRequest;
import com.zaatun.zaatunecommerce.dto.response.ProductResponse;
import com.zaatun.zaatunecommerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.ResultSet;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/products/")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addProduct(@RequestHeader(name = "Authorization") String token,
                                                          @Valid @RequestBody AddProductRequest addProductRequest){
        return productService.addProduct(token, addProductRequest);
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


}
