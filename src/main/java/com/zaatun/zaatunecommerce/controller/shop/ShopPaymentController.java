package com.zaatun.zaatunecommerce.controller.shop;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.SSLCommerzPaymentInfoModel;
import com.zaatun.zaatunecommerce.service.shop.ShopPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("/ipnListener")
    public ResponseEntity<ApiResponse<String>> ipnListener(@RequestParam String status, @RequestParam String tran_date,
                                                           @RequestParam String tran_id, @RequestParam String val_id,
                                                           @RequestParam Double amount, @RequestParam Double store_amount,
                                                           @RequestParam String card_type, @RequestParam String card_no,
                                                           @RequestParam String currency, @RequestParam String bank_tran_id,
                                                           @RequestParam String card_issuer, @RequestParam String card_brand,
                                                           @RequestParam String card_issuer_country,
                                                           @RequestParam String card_issuer_country_code,
                                                           @RequestParam String currency_type, @RequestParam Double currency_amount,
                                                           @RequestParam String value_a, @RequestParam String value_b,
                                                           @RequestParam String value_c, @RequestParam String value_d,
                                                           @RequestParam String verify_sign, @RequestParam String verify_key,
                                                           @RequestParam String risk_level, @RequestParam String risk_title) throws Exception {

        SSLCommerzPaymentInfoModel sslCommerzPaymentInfoModel = new SSLCommerzPaymentInfoModel(UUID.randomUUID().toString(),
                status, tran_date, tran_id, val_id, amount.toString(), store_amount.toString(), currency, bank_tran_id,
                card_type, card_no, card_issuer, card_brand, card_issuer_country, card_issuer_country_code, currency_type,
                currency_amount.toString(), "", "", value_a, value_b, value_c,
                value_d, "", "", "", verify_sign, verify_key, risk_level, risk_title);

        return shopPaymentService.ipnListener(sslCommerzPaymentInfoModel);
    }
}