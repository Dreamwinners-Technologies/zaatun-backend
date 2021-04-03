package com.zaatun.zaatunecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasicTableInfo {
    private String id;

    private String slug;

    private String createBy;

    private Long creationTime;
}
