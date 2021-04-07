package com.zaatun.zaatunecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> {
    int pageSize;
    int pageNo;
    int productCount;
    boolean isLastPage;
    Long totalProduct;
    int totalPages;

    T data;
}
