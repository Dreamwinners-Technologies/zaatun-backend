package com.zaatun.zaatunecommerce.dto.request;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSpecificationRequest {
    private String processor;

    private String battery;

    private String ram;

    private String rom;

    private String operatingSystem;

    private String screenSize;

    private String backCamera;

    private String frontCamera;

    private List<AddDynamicSpecificationRequest> dynamicSpecifications;

}
