package com.zaatun.zaatunecommerce.controller.shop;


import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.service.shop.ShopAffiliateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/store/affiliate")
public class ShopAffiliateController {
    private final ShopAffiliateService shopAffiliateService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> beAffiliate(@RequestHeader(name = "Authorization") String token){
        return shopAffiliateService.beAffiliate(token);
    }

    @PostMapping("/withdraw")
    private ResponseEntity<ApiResponse<String>> makeWithdrawRequest(@RequestHeader(name = "Authorization") String token,
                                                                    @RequestParam Integer amount){
        return  shopAffiliateService.makeWithdrawRequest(token, amount);
    }

}
