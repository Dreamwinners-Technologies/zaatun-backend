package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.model.OrderModel;
import com.zaatun.zaatunecommerce.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public ResponseEntity getAllOrders(String sortBy, Sort.Direction orderBy, int pageNo, int pageSize) {
        OrderModel exampleOrder = OrderModel.builder()
                .build();

        Pageable pageable;
        Sort sort = Sort.by(orderBy, sortBy);
        pageable = PageRequest.of(pageNo, pageSize, sort);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll();
//                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
//                .withMatcher("brand", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<OrderModel> orderModels = orderRepository.findAll(Example.of(exampleOrder, matcher), pageable);

        return new ResponseEntity(orderModels, HttpStatus.OK);
    }
}
