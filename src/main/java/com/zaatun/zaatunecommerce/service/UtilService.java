package com.zaatun.zaatunecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.dto.request.ProductQuantityRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.ProductQuantityModel;
import lombok.AllArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
//        createBy = jwtProvider.getNameFromJwt(token);
        Long creationTime = System.currentTimeMillis();

        String SKU = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 10);

        return new BasicTableInfo(id, slug, SKU, createBy, creationTime);

    }

    public List<ProductQuantityModel> getQuantityModelFromQuantityList(List<ProductQuantityRequest> productQuantityRequests){
        List<ProductQuantityModel> productQuantityModels = new ArrayList<>();

        for (ProductQuantityRequest productQuantityRequest: productQuantityRequests){
            ProductQuantityModel productQuantityModel = new ProductQuantityModel();
            productQuantityModel.setVariant(productQuantityRequest.getVariant());
            productQuantityModel.setQuantity(productQuantityRequest.getQuantity());

            productQuantityModels.add(productQuantityModel);
        }

        return productQuantityModels;
    }


}
