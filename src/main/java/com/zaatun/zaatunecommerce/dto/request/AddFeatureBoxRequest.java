package com.zaatun.zaatunecommerce.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddFeatureBoxRequest {

    private Integer sequenceNo;

    private String title;

    private String subTitle;

    private String link;

    private String bgColor;

    private String showButton;
}
