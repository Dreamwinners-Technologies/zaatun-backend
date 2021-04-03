package com.zaatun.zaatunecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zaatun.zaatunecommerce.dto.BasicTableInfo;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
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

        return new BasicTableInfo(id, slug, createBy, creationTime);

    }


}
