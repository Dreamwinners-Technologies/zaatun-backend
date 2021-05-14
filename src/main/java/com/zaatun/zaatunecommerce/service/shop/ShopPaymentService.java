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

        if (orderModelOptional.isPresent()) {
            String trxId = UUID.randomUUID().toString().toUpperCase().substring(0, 11);

            OrderModel orderModel = orderModelOptional.get();

            orderModel.setTransactionId(trxId);

            SSLCommerz sslCommerz = new SSLCommerz("dokan6070563821cf9", "dokan6070563821cf9@ssl", true);

            StringBuilder products = new StringBuilder();
            for (OrderProductModel orderProductModel : orderModel.getOrderItems()) {
                products.append(orderProductModel.getProductName()).append(",");
            }

            String email;
            if (orderModel.getDeliveryAddress().getEmail() != null) {
                email = orderModel.getDeliveryAddress().getEmail();
            } else {
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
            postData.put("cus_add1", orderModel.getDeliveryAddress().getAddress() + orderModel.getDeliveryAddress().getArea());
            postData.put("cus_city", orderModel.getDeliveryAddress().getCity());
            postData.put("cus_postcode", "");
            postData.put("cus_country", "Bangladesh");
            postData.put("cus_phone", orderModel.getDeliveryAddress().getPhoneNo());
            postData.put("shipping_method", "Courier");
            postData.put("num_of_item", String.valueOf(orderModel.getOrderItems().size()));
            postData.put("product_name", products.toString());
            postData.put("product_profile", "general");


            System.out.println(postData.toString());

            String paymentLink = sslCommerz.initiateTransaction(postData, false);

            orderRepository.save(orderModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Payment Link Generated", paymentLink), HttpStatus.OK);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Order Found with id: " + orderId);
        }

    }

    public ResponseEntity<ApiResponse<String>> ipnListener(SSLCommerzPaymentInfoModel sslCommerzPaymentInfo) throws Exception {
        SSLCommerz sslCommerz = new SSLCommerz("dokan6070563821cf9", "dokan6070563821cf9@ssl", true);

        Map<String, String> formData = new HashMap<>();
        formData.put("tran_id", sslCommerzPaymentInfo.getTran_id());
        formData.put("val_id", sslCommerzPaymentInfo.getVal_id());
        formData.put("amount", sslCommerzPaymentInfo.getAmount());
        formData.put("card_type", sslCommerzPaymentInfo.getCard_type());
        formData.put("store_amount", sslCommerzPaymentInfo.getStore_amount());
        formData.put("card_no", sslCommerzPaymentInfo.getCard_no());
        formData.put("bank_tran_id", sslCommerzPaymentInfo.getBank_tran_id());
        formData.put("tran_date", sslCommerzPaymentInfo.getTran_date());
        formData.put("currency", sslCommerzPaymentInfo.getCurrency());
//        formData.put("bank_tran_id", sslCommerzPaymentInfo.getBank_tran_id());
        formData.put("card_issuer", sslCommerzPaymentInfo.getCard_issuer());
        formData.put("card_brand", sslCommerzPaymentInfo.getCard_brand());
        formData.put("card_issuer_country", sslCommerzPaymentInfo.getCard_issuer_country());
        formData.put("card_issuer_country_code", sslCommerzPaymentInfo.getCard_issuer_country_code());
        formData.put("store_id", sslCommerzPaymentInfo.getStore_id());
        formData.put("verify_sign", sslCommerzPaymentInfo.getVerify_sign());
        formData.put("verify_key", sslCommerzPaymentInfo.getVerify_key());
//        formData.put("cus_fax", sslCommerzPaymentInfo.getCus_fax());
        formData.put("currency_type", sslCommerzPaymentInfo.getCurrency_type());
        formData.put("currency_amount", sslCommerzPaymentInfo.getCurrency_amount());
        formData.put("currency_rate", sslCommerzPaymentInfo.getCurrency_rate());
        formData.put("value_a", sslCommerzPaymentInfo.getValue_a());
        formData.put("value_b", sslCommerzPaymentInfo.getValue_b());
        formData.put("value_c", sslCommerzPaymentInfo.getValue_c());
        formData.put("value_d", sslCommerzPaymentInfo.getValue_d());
        formData.put("risk_level", sslCommerzPaymentInfo.getRisk_level());
        formData.put("risk_title", sslCommerzPaymentInfo.getRisk_title());

        formData.put("base_fair", sslCommerzPaymentInfo.getBase_fair());
        formData.put("status", sslCommerzPaymentInfo.getStatus());

//        System.out.println(sslCommerzPaymentInfo.toString());

        boolean isVerified = sslCommerz.orderValidate(sslCommerzPaymentInfo.getTran_id(),
                sslCommerzPaymentInfo.getAmount(), sslCommerzPaymentInfo.getCurrency(), formData);

        if(isVerified){
            Optional<OrderModel> orderModelOptional = orderRepository.findByOrderId(sslCommerzPaymentInfo.getTran_id());

            if(orderModelOptional.isPresent()){
                OrderModel orderModel = orderModelOptional.get();

                orderModel.setPaymentStatus("Paid");
                orderModel.setPaidAmount(Integer.valueOf(sslCommerzPaymentInfo.getAmount()));
                orderModel.setPaymentMethod(sslCommerzPaymentInfo.getCard_brand());

                orderRepository.save(orderModel);

                return new ResponseEntity<>(new ApiResponse<>(200, "Payment Successful", null), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(new ApiResponse<>(400, "Payment UnSuccessful", null), HttpStatus.BAD_REQUEST);
    }

//    public ResponseEntity<ApiResponse<String>> ipnListenerTest(Map<String, String> allParams) {
//
//        allParams.entrySet().forEach(entry -> {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        });
//
//        return null;
//    }
}
