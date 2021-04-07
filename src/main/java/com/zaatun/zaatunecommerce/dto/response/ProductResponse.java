package com.zaatun.zaatunecommerce.dto.response;

import com.zaatun.zaatunecommerce.model.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    int pageSize;
    int pageNo;
    int productCount;
    boolean isLastPage;
    Long totalProduct;
    int totalPages;

    List<ProductModel> productList;
}
