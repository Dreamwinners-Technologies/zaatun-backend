package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.CouponEditRequest;
import com.zaatun.zaatunecommerce.dto.response.CreateCouponRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.CouponModel;
import com.zaatun.zaatunecommerce.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/dashboard/coupon")
    public ResponseEntity<ApiResponse<String>> createCoupon(@RequestHeader(name = "Authorization") String token,
                                                            @RequestBody CreateCouponRequest createCouponRequest) {
        return couponService.createCoupon(token, createCouponRequest);
    }

    @GetMapping("/dashboard/coupon")
    public ResponseEntity<ApiResponse<PaginationResponse<List<CouponModel>>>> getCouponList(
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) String couponCode,
            @RequestParam(defaultValue = "DESC")Sort.Direction sortDirection){
        return couponService.getCouponList(pageNo, pageSize, couponCode, sortDirection);
    }

    @PutMapping("/dashboard/coupon/{couponCode}")
    public ResponseEntity<ApiResponse<String>> editCoupon(@RequestHeader(name = "Authorization") String token,
                                                          @PathVariable String couponCode,
                                                          @RequestBody CouponEditRequest couponEditRequest){
        return couponService.couponEdit(token, couponCode, couponEditRequest);
    }

    @DeleteMapping("/dashboard/coupon/{couponCode}")
    public ResponseEntity<ApiResponse<String>> deleteCoupon(@PathVariable String couponCode){
        return couponService.couponDelete(couponCode);
    }

    @GetMapping("/shop/validate/coupon")
    public ResponseEntity<ApiResponse<Integer>> validateCoupon(@RequestParam Integer totalPrice,
                                                               @RequestParam String couponCode){
        return couponService.validateCoupon(totalPrice, couponCode);
    }
}
