package com.zaatun.zaatunecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "static_page_model")
public class StaticPageModel {
    @Id
    private String pageId;

    private String title;

    private String pageSlug;

    private Long createdOn;

    private String createdBy;

    private Long updatedOn;

    private String updatedBy;

    private StaticPagePosition position;

    @Column(columnDefinition="TEXT")
    private String content;
}
