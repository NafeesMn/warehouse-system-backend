package com.artiselite.warehouse.dashboard.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.dashboard.dto.DashboardSummaryResponse;
import com.artiselite.warehouse.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary() {
        return ApiResponse.success("Dashboard summary loaded successfully.", dashboardService.getSummary());
    }
}
