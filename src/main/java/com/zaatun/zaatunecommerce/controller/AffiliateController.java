package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AffiliateUserSort;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.ProfileModel;
import com.zaatun.zaatunecommerce.service.AffiliateService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/affiliate")
public class AffiliateController {
    private final AffiliateService affiliateService;

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>>
    getNewAffiliateUsers(@RequestParam(defaultValue = "50") int pageSize,
                         @RequestParam(defaultValue = "0") int pageNo) {
        return affiliateService.getNewAffiliateUsers(pageSize, pageNo);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveAffiliate(@RequestHeader(name = "Authorization") String token,
                                                                @PathVariable String id) {
        return affiliateService.approveAffiliate(token, id);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<List<ProfileModel>>>>
    getAffiliateUsers(@RequestParam(defaultValue = "50") int pageSize,
                      @RequestParam(defaultValue = "0") int pageNo,
                      @RequestParam(required = false) String name,
                      @RequestParam(required = false) String phoneNo,
                      @RequestParam(required = false) String affiliateUserSlug,
                      @RequestParam(required = false, defaultValue = "createdOn") AffiliateUserSort sortBy,
                      @RequestParam(required = false, defaultValue = "DESC")Sort.Direction sortDirection){

        return affiliateService.getAffiliateUserList(pageNo, pageSize, name, phoneNo, affiliateUserSlug, sortBy, sortDirection);
    }

    @GetMapping("/withdraw")
    public ResponseEntity getWithdrawRequests(@RequestParam(defaultValue = "50") int pageSize,
                                              @RequestParam(defaultValue = "0") int pageNo,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String phoneNo,
                                              @RequestParam(required = false) String affiliateUserSlug,
                                              @RequestParam(required = false) AffiliateUserSort sortBy,
                                              @RequestParam(required = false)Sort.Direction sortDirection){
        return affiliateService.getWithdrawRequests();
    }


    @PostMapping("/withdraw/{withdrawId}")
    public ResponseEntity approveWithdrawRequest(@RequestHeader(name = "Authorization") String token,
                                                 @PathVariable Long withdrawId,
                                                 @RequestParam String massage,
                                                 @RequestParam Boolean isApproved){
        return affiliateService.approveWithdrawRequest(token, withdrawId, massage, isApproved);
    }

}
