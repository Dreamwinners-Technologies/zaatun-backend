package com.zaatun.zaatunecommerce.dto.request;

import com.zaatun.zaatunecommerce.model.StaticPagePosition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaticPageRequest {
    private String title;

    private StaticPagePosition position;

    @Column(columnDefinition="TEXT")
    private String content;
}
