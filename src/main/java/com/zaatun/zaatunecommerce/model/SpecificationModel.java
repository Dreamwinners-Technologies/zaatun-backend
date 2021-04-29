package com.zaatun.zaatunecommerce.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "specification_model")
public class SpecificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long specificationId;

    private String processor;

    private String battery;

    private String ram;

    private String rom;

    private String operatingSystem;

    private String screenSize;

    private String backCamera;

    private String frontCamera;

    @OneToMany(cascade = CascadeType.ALL)
    List<DynamicSpecificationModel> dynamicSpecifications;
}
