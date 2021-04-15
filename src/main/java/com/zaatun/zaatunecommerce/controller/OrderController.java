package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity getAllOrders(@RequestParam(defaultValue = "createdOn") String sortBy,
                                       @RequestParam(defaultValue = "ASC") Sort.Direction orderBy,
                                       @RequestParam(defaultValue = "50") int pageSize,
                                       @RequestParam(defaultValue = "0") int pageNo) {

        return orderService.getAllOrders(sortBy, orderBy, pageNo, pageSize);
    }
}
