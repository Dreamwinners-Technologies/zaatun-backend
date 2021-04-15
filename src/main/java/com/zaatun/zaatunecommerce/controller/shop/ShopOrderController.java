package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.OrderPlaceRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderResponse;
import com.zaatun.zaatunecommerce.service.shop.ShopOrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/store/orders")
public class ShopOrderController {
    private final ShopOrderService shopOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<String >> placeOrder(@RequestHeader(name = "Authorization") String token,
                                                           @Valid @RequestBody OrderPlaceRequest orderPlaceRequest){
        return shopOrderService.placeOrder(orderPlaceRequest, token);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<List<ShopOrderResponse>>>> getOrderInfos(@RequestHeader(name = "Authorization") String token,
                                                                                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                                                                                  @RequestParam(defaultValue = "30") Integer pageSize){

        return shopOrderService.getOrderInfos(token, pageNo, pageSize);
    }
}
