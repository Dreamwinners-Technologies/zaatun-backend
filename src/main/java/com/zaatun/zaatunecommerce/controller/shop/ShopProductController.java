package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopProductResponse;
import com.zaatun.zaatunecommerce.service.shop.ShopProductService;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopProductResponse>>>> getProducts(@RequestParam(required = false) String productName, String brand,
                                                                                                  String categoryId, String subCategoryId, String productSlug,
                                                                                                  Boolean inStock, Boolean isFeatured, String processor,
                                                                                                  String battery, String ram,
                                                                                                  String rom, String screenSize, String backCamera, String frontCamera,
                                                                                                  @RequestParam(defaultValue = "createdOn") String sortBy,
                                                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction orderBy,
                                                                                                  @RequestParam(defaultValue = "50") int pageSize,
                                                                                                  @RequestParam(defaultValue = "0") int pageNo,
                                                                                                  @RequestParam(required = false) Integer rating ){

        return shopProductService.getProducts(productName, brand, categoryId, subCategoryId, productSlug, inStock,
                isFeatured, processor, battery, ram, rom, screenSize, backCamera, frontCamera, sortBy, orderBy,
                pageSize, pageNo, rating );
    }
}
