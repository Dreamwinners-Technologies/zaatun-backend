package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopCategoryResponse;
import com.zaatun.zaatunecommerce.service.CategoryService;
import com.zaatun.zaatunecommerce.service.shop.ShopCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store/categories/")
public class ShopCategoryController {
    private final ShopCategoryService shopCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopCategoryResponse>>> getCategories(){
        return shopCategoryService.getCategories();
    }
}
