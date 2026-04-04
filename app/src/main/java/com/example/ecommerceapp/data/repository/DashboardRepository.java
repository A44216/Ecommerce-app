package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.DashboardService;
import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;

import retrofit2.Call;

public class DashboardRepository {

    private DashboardService dashboardService;

    public DashboardRepository(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public Call<DashboardResponse> getDashboard(int shopId) {
        return dashboardService.getDashboard(shopId);
    }
}
