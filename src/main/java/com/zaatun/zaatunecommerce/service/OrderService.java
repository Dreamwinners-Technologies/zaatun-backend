package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.OrderProcessRequest;
import com.zaatun.zaatunecommerce.dto.response.OrderResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.dto.response.shop.ShopOrderProductResponse;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.*;
import com.zaatun.zaatunecommerce.repository.OrderRepository;
import com.zaatun.zaatunecommerce.service.shop.ShopProductHelperService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ShopProductHelperService shopProductHelperService;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<PaginationResponse<List<OrderResponse>>>>
    getAllOrders(String sortBy, boolean isCompleted, Sort.Direction sortDirection, int pageNo, int pageSize,
                 String orderId, String customerName, String customerPhoneNo, String area, String city, String orderStatus) {


        DeliveryAddressModel deliveryAddressModelExample = DeliveryAddressModel.builder()
                .fullName(customerName)
                .phoneNo(customerPhoneNo)
                .area(area)
                .city(city)
                .build();

        OrderModel exampleOrder = OrderModel.builder()
                .isCompleted(isCompleted)
                .orderId(orderId)
                .deliveryAddress(deliveryAddressModelExample)
                .orderStatus(orderStatus)
                .build();

        Pageable pageable;
        Sort sort = Sort.by(sortDirection, sortBy);
        pageable = PageRequest.of(pageNo, pageSize, sort);

        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("deliveryAddress.fullName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("deliveryAddress.phoneNo", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("deliveryAddress.area", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("deliveryAddress.city", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("orderId", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("orderStatus", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Page<OrderModel> orderModelPageable = orderRepository.findAll(Example.of(exampleOrder, matcher), pageable);

        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (OrderModel orderModel : orderModelPageable.getContent()) {
            List<OrderProductModel> orderProductModels = orderModel.getOrderItems();

            OrderResponse orderResponse = new OrderResponse(orderModel.getId(), orderModel.getOrderId(), orderModel.getInvoiceId(),
                    orderModel.getCreateBy(), orderModel.getCreatedOn(), orderModel.getUpdatedBy(), orderModel.getUpdatedOn(),
                    orderModel.getUserName(), orderProductModels, orderModel.getDeliveryAddress(), orderModel.getOrderStatus(),
                    orderModel.getProductPriceTotal(), orderModel.getPaidAmount(), orderModel.getPaymentMethod(),
                    orderModel.getPaymentStatus(), orderModel.getShippingCharge(), orderModel.getAdminDiscount(),
                    orderModel.getAdminDiscountAddedBy(), orderModel.getCouponDiscount(), orderModel.getSubTotal(),
                    orderModel.getTotalAmount(), orderModel.getTransactionId(), orderModel.getCouponModel(),
                    orderModel.getOrderProcessHistory(), orderModel.getIsCompleted());

            orderResponseList.add(orderResponse);
        }

        PaginationResponse<List<OrderResponse>> paginationResponse =
                new PaginationResponse<>(pageSize, pageNo, orderModelPageable.getContent().size(), orderModelPageable.isLast(),
                        orderModelPageable.getTotalElements(), orderModelPageable.getTotalPages(), orderResponseList);

        if (orderModelPageable.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Orders Found", paginationResponse), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Orders Found", paginationResponse), HttpStatus.OK);
        }

    }

    public ResponseEntity<ApiResponse<String>> addOrderStatus(String token, String orderId, OrderProcessRequest orderProcessRequest) {
        Optional<OrderModel> orderModelOptional = orderRepository.findById(orderId);

        if (orderModelOptional.isPresent()) {
            OrderModel orderModel = orderModelOptional.get();
            String updatedBy = jwtProvider.getNameFromJwt(token);
            Long updateOn = System.currentTimeMillis();

            if (orderProcessRequest.getOrderStatus().toString().equalsIgnoreCase("delivered") ||
                    orderProcessRequest.getOrderStatus().toString().equalsIgnoreCase("canceled")) {
                orderModel.setIsCompleted(true);
            }

            List<OrderProcessHistoryModel> orderProcessHistoryModels = orderModel.getOrderProcessHistory();

            OrderProcessHistoryModel orderProcessHistoryModel = OrderProcessHistoryModel.builder()
                    .updateBy(updatedBy)
                    .updatedOn(updateOn)
                    .orderStatus(orderProcessRequest.getOrderStatus().toString())
                    .employeeNote(orderProcessRequest.getEmployeeNote())
                    .customerNote(orderProcessRequest.getCustomerNote())
                    .build();

            orderProcessHistoryModels.add(orderProcessHistoryModel);
            orderModel.setOrderProcessHistory(orderProcessHistoryModels);
            orderModel.setOrderStatus(orderProcessRequest.getOrderStatus().toString());
            orderModel.setUpdatedBy(updatedBy);
            orderModel.setUpdatedOn(updateOn);

            orderRepository.save(orderModel);

            return new ResponseEntity<>(new ApiResponse<>(201, "Updated Order Status Successful",
                    orderProcessRequest.getOrderStatus().toString()), HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found with id: " + orderId);
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteOrder(String orderId) {
        Optional<OrderModel> orderModelOptional = orderRepository.findById(orderId);

        if (orderModelOptional.isPresent()) {
            orderRepository.deleteById(orderId);
            return new ResponseEntity<>(new ApiResponse<>(200, "Order Delete Successful", null), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Order Found with id: " + orderId);
        }
    }
}
