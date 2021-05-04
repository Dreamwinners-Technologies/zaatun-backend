package com.zaatun.zaatunecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

@Service
public class ImageUtilService {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String cloudApiKey;
    @Value("${cloudinary.api_secret}")
    private String cloudApiSecret;

    public List<String> uploadImage(MultipartFile[] aFile) {

        Cloudinary c = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", cloudApiKey,
                "api_secret", cloudApiSecret));

        List<String> photoLinksList = new ArrayList<>();

        try {
            if (aFile.length < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No File Found");
            }

            for (MultipartFile mpFile : aFile) {
                File f = Files.createTempFile("temp", mpFile.getOriginalFilename()).toFile();
                mpFile.transferTo(f);
                Map response = c.uploader().upload(f, ObjectUtils.emptyMap());
                JSONObject json = new JSONObject(response);
                String url = json.getString("url");

                url = url.replaceAll("http://", "https://");

                photoLinksList.add(url);

            }

            return photoLinksList;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "upload Failed" + e);
        }
    }
}
