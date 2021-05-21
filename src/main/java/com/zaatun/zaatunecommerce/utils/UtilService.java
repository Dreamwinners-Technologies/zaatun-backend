package com.zaatun.zaatunecommerce.utils;

import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.ProductQuantityRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.ProductVariantModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UtilService {
    private final JwtProvider jwtProvider;


    public BasicTableInfo generateBasicTableInfo(String name, String token){

        String id = UUID.randomUUID().toString();
        String slug = name.toLowerCase().replace(" ", "-")
                + "-" + id.substring(0, 4);
        String createBy = null;
        createBy = jwtProvider.getNameFromJwt(token);
        Long creationTime = System.currentTimeMillis();

        String SKU = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);

        return new BasicTableInfo(id, slug, SKU, createBy, creationTime);

    }

    public List<ProductVariantModel> getQuantityModelFromQuantityList(List<ProductQuantityRequest> productQuantityRequests){
        List<ProductVariantModel> productVariantModels = new ArrayList<>();

        for (ProductQuantityRequest productQuantityRequest: productQuantityRequests){
            ProductVariantModel productVariantModel = new ProductVariantModel();
            productVariantModel.setVariant(productQuantityRequest.getVariant());
            productVariantModel.setQuantity(productQuantityRequest.getQuantity());

            productVariantModels.add(productVariantModel);
        }

        return productVariantModels;
    }


}
