package com.zaatun.zaatunecommerce.controller;

import com.zaatun.zaatunecommerce.dto.ApiResponse;
import com.zaatun.zaatunecommerce.model.ShortStatisticsModel;
import com.zaatun.zaatunecommerce.service.DashboardInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class DashboardInfoController {
    private final DashboardInfoService dashboardInfoService;

    @PostMapping("/shortStat")
    public ResponseEntity<ApiResponse<String>> createShortStat(){
        return dashboardInfoService.createShortStat();
    }

    @GetMapping("/shortStat")
    public ResponseEntity<ApiResponse<ShortStatisticsModel>> getShortStatistics(){
        return dashboardInfoService.getShortStatics();
    }

}
