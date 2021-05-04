package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.dto.request.AddFeatureBoxRequest;
import com.zaatun.zaatunecommerce.dto.request.EditFeatureBoxRequest;
import com.zaatun.zaatunecommerce.model.FeatureBoxModel;
import com.zaatun.zaatunecommerce.service.FeatureBoxService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard/featureBox")
public class FeatureBoxController {
    private final FeatureBoxService featureBoxService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeatureBoxModel>>> getFeatureBoxList(){
        return featureBoxService.getFeatureBoxList();
    }


    @PostMapping
    public ResponseEntity<ApiResponse<String>> addNewFeatureBox(@RequestBody AddFeatureBoxRequest addFeatureBoxRequest,
                                                                @RequestHeader(name = "Authorization") String token){

        return featureBoxService.addNewFeatureBox(token, addFeatureBoxRequest);
    }

    @PutMapping("/{sequenceNo}")
    public ResponseEntity<ApiResponse<String>> editFeatureBox(@PathVariable Integer sequenceNo,
                                                              @RequestBody EditFeatureBoxRequest editFeatureBoxRequest,
                                                              @RequestHeader(name = "Authorization") String token){
        return featureBoxService.editFeatureBox(sequenceNo, editFeatureBoxRequest, token);
    }

    @PostMapping("/image/{sequenceNo}")
    public ResponseEntity<ApiResponse<String>> addImageToFeatureBox(@PathVariable Integer sequenceNo,
                                                                    @RequestHeader(name = "Authorization") String token,
                                                                    MultipartFile mpFiles){
        return featureBoxService.addImageToFeatureBox(sequenceNo, token, mpFiles);
    }

}
