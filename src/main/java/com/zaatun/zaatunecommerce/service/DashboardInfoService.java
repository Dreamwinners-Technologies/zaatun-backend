package com.zaatun.zaatunecommerce.service;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.ShortStatisticsModel;
import com.zaatun.zaatunecommerce.repository.ShortStatisticsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DashboardInfoService {
    private final ShortStatisticsRepository shortStatisticsRepository;


    public ResponseEntity<ApiResponse<String>> createShortStat() {
        Optional<ShortStatisticsModel> shortStatisticsModelOptional = shortStatisticsRepository.findById(0);
        if (shortStatisticsModelOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One Static is already created.You can't add more.");
        } else {
            ShortStatisticsModel shortStatisticsModel = new ShortStatisticsModel(0, 0, 0,
                    0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0);

            shortStatisticsRepository.save(shortStatisticsModel);
            return new ResponseEntity<>(new ApiResponse<>(201, "Created", null), HttpStatus.CREATED);
        }
    }


    public ResponseEntity<ApiResponse<ShortStatisticsModel>> getShortStatics() {
        Optional<ShortStatisticsModel> shortStatisticsModelOptional = shortStatisticsRepository.findById(0);
        if (shortStatisticsModelOptional.isPresent()) {
            ShortStatisticsModel shortStatisticsModel = shortStatisticsModelOptional.get();

            return new ResponseEntity<>(new ApiResponse<>(200, "Short Statistics Found", shortStatisticsModel), HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Statistics found. Please create one first");
        }
    }
}
