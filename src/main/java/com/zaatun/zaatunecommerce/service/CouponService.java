package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.CouponEditRequest;
import com.zaatun.zaatunecommerce.dto.response.CreateCouponRequest;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.CouponModel;
import com.zaatun.zaatunecommerce.repository.CouponRepository;
import com.zaatun.zaatunecommerce.service.shop.ShopOrderServiceExtended;
import com.zaatun.zaatunecommerce.utils.UtilService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final UtilService utilService;
    private final ShopOrderServiceExtended shopOrderServiceExtended;

    public ResponseEntity<ApiResponse<String>> createCoupon(String token, CreateCouponRequest createCouponRequest) {
        BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

        CouponModel couponModel = new CouponModel(basicTableInfo.getId(), createCouponRequest.getCouponCode(),
                basicTableInfo.getCreateBy(), basicTableInfo.getCreationTime(), null, null,
                createCouponRequest.getCouponAmount(), createCouponRequest.getMinimumBuy(),
                createCouponRequest.getCouponDetails(), createCouponRequest.getValidFrom(), createCouponRequest.getValidTill());

        couponRepository.save(couponModel);

        return new ResponseEntity<>(new ApiResponse<>(201,"Coupon Created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<PaginationResponse<List<CouponModel>>>> getCouponList(int pageNo, int pageSize, String couponCode, Sort.Direction sortDirection) {
        CouponModel couponExample = CouponModel.builder().couponCode(couponCode).build();
        Sort sort = Sort.by(sortDirection, "createdOn");
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<CouponModel> couponModelPage = couponRepository.findAll(Example.of(couponExample), pageable);

        PaginationResponse<List<CouponModel>> paginationResponse = new PaginationResponse<>(pageSize, pageNo, couponModelPage.getContent().size(),
                couponModelPage.isLast(), couponModelPage.getTotalElements(), couponModelPage.getTotalPages(),
                couponModelPage.getContent());

        return new ResponseEntity<>(new ApiResponse<>(200, "Coupon Found", paginationResponse), HttpStatus.OK);

    }


    public ResponseEntity<ApiResponse<Integer>> validateCoupon(Integer totalPrice, String couponCode) {
        CouponModel couponModel = shopOrderServiceExtended.getCouponDiscount(totalPrice, couponCode);

        Integer couponDiscount = couponModel.getCouponAmount();

        return new ResponseEntity<>(new ApiResponse<>(200, "Coupon is valid", couponDiscount), HttpStatus.OK);

    }

    public ResponseEntity<ApiResponse<String>> couponEdit(String token, String couponCode, CouponEditRequest couponEditRequest) {
        Optional<CouponModel> couponModelOptional = couponRepository.findByCouponCode(couponCode);

        if(couponModelOptional.isPresent()){
            CouponModel couponModel = couponModelOptional.get();

            BasicTableInfo basicTableInfo = utilService.generateBasicTableInfo("", token);

            couponModel.setUpdatedBy(basicTableInfo.getCreateBy());
            couponModel.setUpdatedOn(basicTableInfo.getCreationTime());
            couponModel.setCouponAmount(couponEditRequest.getCouponAmount());
            couponModel.setCouponDetails(couponEditRequest.getCouponDetails());
            couponModel.setMinimumBuy(couponEditRequest.getMinimumBuy());
            couponModel.setValidFrom(couponEditRequest.getValidFrom());
            couponModel.setValidTill(couponModel.getValidTill());

            couponRepository.save(couponModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Edit Successful", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon Code Not Found");
        }
    }


    public ResponseEntity<ApiResponse<String>> couponDelete(String couponCode) {
        Optional<CouponModel> couponModelOptional = couponRepository.findByCouponCode(couponCode);

        if(couponModelOptional.isPresent()){
            couponRepository.delete(couponModelOptional.get());

            return new ResponseEntity<>(new ApiResponse<>(200, "Delete Successful", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon Code Not Found");
        }
    }
}
