package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.SSLCommerzPaymentInfoModel;
import com.zaatun.zaatunecommerce.service.shop.ShopPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store/pay")
public class ShopPaymentController {
    private final ShopPaymentService shopPaymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> initiatePayment(@RequestHeader(name = "Authorization") String token,
                                                               @PathVariable String orderId) throws Exception {
        return shopPaymentService.initiatePayment(token, orderId);
    }

//    @PostMapping("/ipnListener")
//    public ResponseEntity<ApiResponse<String>> ipnListener(@RequestParam Map<String, String> allParams) throws Exception {
//
//
//        return shopPaymentService.ipnListenerTest(allParams);
//    }

    @PostMapping("/ipnListener")
    public ResponseEntity<ApiResponse<String>> ipnListener(
            @RequestParam(required = false, defaultValue = "") String tran_id, @RequestParam(required = false, defaultValue = "") String val_id,
            @RequestParam(required = false, defaultValue = "") String amount, @RequestParam(required = false, defaultValue = "") String card_type,
            @RequestParam(required = false, defaultValue = "") String store_amount, @RequestParam(required = false, defaultValue = "") String card_no,
            @RequestParam(required = false, defaultValue = "") String bank_tran_id, @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "") String tran_date, @RequestParam(required = false, defaultValue = "") String currency,
            @RequestParam(required = false, defaultValue = "") String card_issuer, @RequestParam(required = false, defaultValue = "") String card_brand,
            @RequestParam(required = false, defaultValue = "") String card_issuer_country, @RequestParam(required = false, defaultValue = "") String card_issuer_country_code,
            @RequestParam(required = false, defaultValue = "") String store_id, @RequestParam(required = false, defaultValue = "") String verify_sign,
            @RequestParam(required = false, defaultValue = "") String verify_key, @RequestParam(required = false, defaultValue = "") String currency_type,
            @RequestParam(required = false, defaultValue = "") String currency_amount, @RequestParam(required = false, defaultValue = "") String currency_rate,
            @RequestParam(required = false, defaultValue = "") String base_fair, @RequestParam(required = false, defaultValue = "") String value_a, @RequestParam String value_b,
            @RequestParam(required = false, defaultValue = "") String value_c, @RequestParam(required = false, defaultValue = "") String value_d,
            @RequestParam(required = false, defaultValue = "") String risk_level, @RequestParam(required = false, defaultValue = "") String risk_title) throws Exception {

        SSLCommerzPaymentInfoModel sslCommerzPaymentInfoModel = new SSLCommerzPaymentInfoModel(UUID.randomUUID().toString(),
                status, tran_date, tran_id, val_id, amount, store_amount, currency, bank_tran_id,
                card_type, card_no, card_issuer, card_brand, card_issuer_country, card_issuer_country_code, currency_type,
                currency_amount, currency_rate, base_fair, value_a, value_b, value_c,
                value_d, "", "", "", verify_sign, verify_key, risk_level, risk_title, store_id);

        return shopPaymentService.ipnListener(sslCommerzPaymentInfoModel);
    }
}
