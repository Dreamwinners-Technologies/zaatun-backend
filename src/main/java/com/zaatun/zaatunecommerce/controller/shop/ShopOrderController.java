package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.service.shop.ShopOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/store/orders")
public class ShopOrderController {
    private final ShopOrderService shopOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<String >> placeOrder(@RequestHeader(name = "Authorization") String token,
                                                           @RequestBody OrderPlaceRequest orderPlaceRequest){
        return shopOrderService.placeOrder(orderPlaceRequest, token);
    }
}
