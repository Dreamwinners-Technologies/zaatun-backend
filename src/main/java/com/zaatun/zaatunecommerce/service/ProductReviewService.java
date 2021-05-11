package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.response.PaginationResponse;
import com.zaatun.zaatunecommerce.model.ProductModel;
import com.zaatun.zaatunecommerce.model.ProductReviewModel;
import com.zaatun.zaatunecommerce.repository.ProductReviewRepository;
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
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    public ResponseEntity<ApiResponse<PaginationResponse<List<ProductReviewModel>>>> getAllReview(int pageSize, int pageNo, String productSlug, String reviewBy) {

        ProductReviewModel exampleReview = ProductReviewModel.builder()
                .reviewerUserName(reviewBy)
                .productModel(ProductModel.builder().productSlug(productSlug).build())
                .build();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductReviewModel> productReviewPage = productReviewRepository.findAll(Example.of(exampleReview), pageable);

        PaginationResponse<List<ProductReviewModel>> paginationResponse = new PaginationResponse<>(pageSize, pageNo,
                productReviewPage.getContent().size(), productReviewPage.isLast(), productReviewPage.getTotalElements(),
                productReviewPage.getTotalPages(), productReviewPage.getContent());

        return new ResponseEntity<>(new ApiResponse<>(200, "Review Found", paginationResponse), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<String>> deleteReview(Long reviewId) {
        Optional<ProductReviewModel> productReviewModelOptional = productReviewRepository.findById(reviewId);
        if(productReviewModelOptional.isPresent()){
            productReviewRepository.deleteById(reviewId);

            return new ResponseEntity<>(new ApiResponse<>(200, "Review Deleted", null), HttpStatus.OK);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review Not Found");
        }
    }
}
