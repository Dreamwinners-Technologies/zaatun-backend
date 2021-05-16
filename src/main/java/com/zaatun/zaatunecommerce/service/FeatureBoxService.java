package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AddFeatureBoxRequest;
import com.zaatun.zaatunecommerce.dto.request.EditFeatureBoxRequest;
import com.zaatun.zaatunecommerce.jwt.security.jwt.JwtProvider;
import com.zaatun.zaatunecommerce.model.FeatureBoxModel;
import com.zaatun.zaatunecommerce.repository.FeatureBoxRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FeatureBoxService {
    private final FeatureBoxRepository featureBoxRepository;
    private final JwtProvider jwtProvider;
    private final ImageUtilService imageUtilService;

    public ResponseEntity<ApiResponse<String>> addNewFeatureBox(String token, AddFeatureBoxRequest addFeatureBoxRequest) {
        String username = jwtProvider.getUserNameFromJwt(token);
        Long createdOn = System.currentTimeMillis();

        FeatureBoxModel featureBoxModel = new FeatureBoxModel(0L, createdOn, username, addFeatureBoxRequest.getSequenceNo(),
                addFeatureBoxRequest.getTitle(), addFeatureBoxRequest.getSubTitle(), addFeatureBoxRequest.getLink(),
                addFeatureBoxRequest.getBgColor(), null, addFeatureBoxRequest.getShowButton());

        featureBoxRepository.save(featureBoxModel);

        return new ResponseEntity<>(new ApiResponse<>(201, "Feature Box Created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse<String>> editFeatureBox(Integer sequenceId, EditFeatureBoxRequest editFeatureBoxRequest, String token) {
        Optional<FeatureBoxModel> featureBoxModelOptional = featureBoxRepository.findBySequenceNo(sequenceId);

        if (featureBoxModelOptional.isPresent()) {
            String username = jwtProvider.getUserNameFromJwt(token);
            Long updatedOn = System.currentTimeMillis();

            FeatureBoxModel featureBoxModel = featureBoxModelOptional.get();
            featureBoxModel.setUpdatedOn(updatedOn);
            featureBoxModel.setUpdatedBy(username);
            featureBoxModel.setTitle(editFeatureBoxRequest.getTitle());
            featureBoxModel.setSubTitle(editFeatureBoxRequest.getSubTitle());
            featureBoxModel.setLink(editFeatureBoxRequest.getLink());
            featureBoxModel.setBgColor(editFeatureBoxRequest.getBgColor());
            featureBoxModel.setShowButton(editFeatureBoxRequest.getShowButton());

            featureBoxRepository.save(featureBoxModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Feature Box Edit Successful", null), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Feature Box Found with that sequence");
        }

    }

    public ResponseEntity<ApiResponse<String>> addImageToFeatureBox(Integer sequenceNo, String token, MultipartFile mpFiles) {
        Optional<FeatureBoxModel> featureBoxModelOptional = featureBoxRepository.findBySequenceNo(sequenceNo);

        if (featureBoxModelOptional.isPresent()) {
            String username = jwtProvider.getUserNameFromJwt(token);
            Long updatedOn = System.currentTimeMillis();

            FeatureBoxModel featureBoxModel = featureBoxModelOptional.get();
            featureBoxModel.setUpdatedOn(updatedOn);
            featureBoxModel.setUpdatedBy(username);

            MultipartFile[] multipartFiles = new MultipartFile[1];
            multipartFiles[0] = mpFiles;

            List<String> imageLinks = imageUtilService.uploadImage(multipartFiles);

            featureBoxModel.setImageLink(imageLinks.get(0));

            featureBoxRepository.save(featureBoxModel);

            return new ResponseEntity<>(new ApiResponse<>(200, "Feature Box Image Upload Successful", null), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Feature Box Found with that sequence");
        }
    }

    public ResponseEntity<ApiResponse<List<FeatureBoxModel>>> getFeatureBoxList() {
        Sort sort = Sort.by(Sort.Direction.ASC, "sequenceNo");
        List<FeatureBoxModel> featureBoxModels = featureBoxRepository.findAll(sort);

        if (featureBoxModels.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(200, "No Feature Box Found", featureBoxModels),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(200, "Feature Box Found", featureBoxModels),
                    HttpStatus.OK);
        }
    }
}
