package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.OrderProcessRequest;
import com.zaatun.zaatunecommerce.dto.response.OrderResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<List<OrderResponse>>>> getAllOrders(
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) String orderId,
            String customerName, String customerPhoneNo, String area,
            String city, String orderStatus
    ) {

        return orderService.getAllOrders(sortBy, false, sortDirection, pageNo,
                pageSize, orderId, customerName, customerPhoneNo, area, city, orderStatus);
    }

    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<PaginationResponse<List<OrderResponse>>>> getAllCompletedOrders(
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(required = false) String orderId,
            String customerName, String customerPhoneNo, String area,
            String city, String orderStatus
    ) {

        return orderService.getAllOrders(sortBy,true, sortDirection, pageNo,
                pageSize, orderId, customerName, customerPhoneNo, area, city, orderStatus);
    }

    @PostMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<String>> addOrderStatus(@RequestHeader(name = "Authorization") String token,
                                         @PathVariable String orderId,
                                         @RequestBody OrderProcessRequest orderProcessRequest){
        return orderService.addOrderStatus(token, orderId, orderProcessRequest);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable String orderId){
        return orderService.deleteOrder(orderId);
    }
}
