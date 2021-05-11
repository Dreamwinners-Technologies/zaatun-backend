package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopStaticPageResponse;
import com.zaatun.zaatunecommerce.service.StaticPageService;
import com.zaatun.zaatunecommerce.service.shop.ShopStaticPageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/shop/staticPages")
public class ShopStaticPageController {
    private final ShopStaticPageService shopStaticPageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopStaticPageResponse>>> getStaticPageList(){
        return shopStaticPageService.getStaticPageList();
    }
    
    @GetMapping("/{pageSlug}")
    public ResponseEntity<ApiResponse<ShopStaticPageResponse>> getStaticPageSlug(@PathVariable String pageSlug){
        return shopStaticPageService.getStaticPageBySlug(pageSlug);
    }
}
