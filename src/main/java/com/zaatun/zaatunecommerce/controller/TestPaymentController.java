package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.sslComPay.SSLCommerz;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/test/payment")
public class TestPaymentController {



    @PostMapping("/init")
    public String initPayment() throws Exception {
        String trxId = UUID.randomUUID().toString().toUpperCase().substring(0,11);

        SSLCommerz sslCommerz = new SSLCommerz("dokan6070563821cf9","dokan6070563821cf9@ssl", true);

        Map<String, String> postData = new HashMap<>();

        postData.put("total_amount", "1000");
        postData.put("currency", "BDT");
        postData.put("tran_id", trxId);
        postData.put("product_category", "Gadgets");
        postData.put("success_url", "https://fb.com/aalhabib001");
        postData.put("fail_url", "https://twitter.com/aalhabib001");
        postData.put("cancel_url", "https://github.com/aalhabib001");
        postData.put("emi_option", "0");
        postData.put("cus_name", "Habib");
        postData.put("cus_email", "hjhabib24@gmail.com");
        postData.put("cus_add1", "Ghonar Chala, Kachua, Sakhipur");
        postData.put("cus_city", "Tangail");
        postData.put("cus_postcode", "1951");
        postData.put("cus_country", "Bangladesh");
        postData.put("cus_phone", "01515212687");
        postData.put("shipping_method", "PFL");
        postData.put("num_of_item", "1");
        postData.put("product_name", "Note 7 3/32");
//        postData.put("product_category", "Mobile");
        postData.put("product_profile", "general");

//        sslCommerz.(postData,false)

        return sslCommerz.initiateTransaction(postData,false);
    }

}
