package com.zaatun.zaatunecommerce.dto.request;

import com.zaatun.zaatunecommerce.model.ProductAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAttributesRequest {
    private String attributeName;

    private List<String> values;
}
