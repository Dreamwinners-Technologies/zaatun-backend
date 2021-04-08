package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.shop.ShopDeliveryAddressRequest;
import com.zaatun.zaatunecommerce.model.DeliveryAddressModel;
import com.zaatun.zaatunecommerce.service.shop.DeliveryAddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store/address")
public class ShopDeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    private ResponseEntity<ApiResponse<Long>> addDeliveryAddress(@RequestHeader(name = "Authorization") String token,
            @RequestBody ShopDeliveryAddressRequest shopDeliveryAddressRequest){

        return deliveryAddressService.addDeliveryAddress(token, shopDeliveryAddressRequest);
    }

    @GetMapping
    private ResponseEntity<ApiResponse<List<DeliveryAddressModel>>> getDeliveryAddress(@RequestHeader(name = "Authorization") String token){

        return deliveryAddressService.getDeliveryAddress(token);
    }

    @PutMapping("/{addressId}")
    private ResponseEntity<ApiResponse<List<DeliveryAddressModel>>> editDeliveryAddress(@RequestHeader(name = "Authorization") String token,
                                                                                        @RequestBody ShopDeliveryAddressRequest shopDeliveryAddressRequest,
                                                                                        @PathVariable Long addressId){

        return deliveryAddressService.editDeliveryAddress(token, shopDeliveryAddressRequest, addressId);
    }
}
