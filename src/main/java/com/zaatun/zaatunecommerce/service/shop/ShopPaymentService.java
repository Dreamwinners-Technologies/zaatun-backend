package com.zaatun.zaatunecommerce.service.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.OrderModel;
import com.zaatun.zaatunecommerce.model.OrderProductModel;
import com.zaatun.zaatunecommerce.model.SSLCommerzPaymentInfoModel;
import com.zaatun.zaatunecommerce.repository.OrderRepository;
import com.zaatun.zaatunecommerce.sslComPay.SSLCommerz;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShopPaymentService {
    private final OrderRepository orderRepository;

    public ResponseEntity<ApiResponse<String>> initiatePayment(String token, String orderId) throws Exception {
        Optional<OrderModel> orderModelOptional = orderRepository.findByOrderId(orderId);

        if(orderModelOptional.isPresent()){
            String trxId = UUID.randomUUID().toString().toUpperCase().substring(0,11);

            OrderModel orderModel = orderModelOptional.get();

            orderModel.setTransactionId(trxId);

            SSLCommerz sslCommerz = new SSLCommerz("dokan6070563821cf9","dokan6070563821cf9@ssl", true);

            StringBuilder products = new StringBuilder();
            for (OrderProductModel orderProductModel: orderModel.getOrderItems()){
                products.append(orderProductModel.getProductName()).append(",");
            }

            String email;
            if(orderModel.getDeliveryAddress().getEmail() != null){
                email = orderModel.getDeliveryAddress().getEmail();
            }
            else {
                email = "hjhabib24@gmail.com";
            }

            Map<String, String> postData = new HashMap<>();
            postData.put("total_amount", orderModel.getTotalAmount().toString());
            postData.put("currency", "BDT");
            postData.put("tran_id", trxId);
            postData.put("product_category", "Gadgets");
            postData.put("success_url", "https://fb.com/aalhabib001");
            postData.put("fail_url", "https://twitter.com/aalhabib001");
            postData.put("cancel_url", "https://github.com/aalhabib001");
            postData.put("emi_option", "0");
            postData.put("cus_name", orderModel.getDeliveryAddress().getFullName());
            postData.put("cus_email", email);
            postData.put("cus_add1", orderModel.getDeliveryAddress().getAddress()+orderModel.getDeliveryAddress().getArea());
            postData.put("cus_city", orderModel.getDeliveryAddress().getCity());
            postData.put("cus_postcode", "");
            postData.put("cus_country", "Bangladesh");
            postData.put("cus_phone", orderModel.getDeliveryAddress().getPhoneNo());
            postData.put("shipping_method", "Courier");
            postData.put("num_of_item", String.valueOf(orderModel.getOrderItems().size()));
            postData.put("product_name", products.toString());
            postData.put("product_profile", "general");


            System.out.println(postData.toString());

            String paymentLink = sslCommerz.initiateTransaction(postData,false);

            orderRepository.save(orderModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Payment Link Generated", paymentLink), HttpStatus.OK);

        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Order Found with id: "+ orderId);
        }

    }

    public ResponseEntity<ApiResponse<String>> ipnListener(SSLCommerzPaymentInfoModel sslCommerzPaymentInfoModel) throws Exception {
        SSLCommerz sslCommerz = new SSLCommerz("dokan6070563821cf9","dokan6070563821cf9@ssl", true);

        System.out.println(sslCommerzPaymentInfoModel.toString());

        Map<String, String> formData = new HashMap<>();
        formData.put("verify_key", sslCommerzPaymentInfoModel.getVerify_key());
        formData.put("verify_sign", sslCommerzPaymentInfoModel.getVerify_sign());
        formData.put("val_id", sslCommerzPaymentInfoModel.getVal_id());

        boolean isVerified = sslCommerz.orderValidate(sslCommerzPaymentInfoModel.getTran_id(),
                sslCommerzPaymentInfoModel.getAmount(),
                sslCommerzPaymentInfoModel.getCurrency(), formData);

        System.out.println(isVerified);

        return null;
    }
}
