package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.DashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;
import com.example.ecommerceapp.data.model.response.dashboard.RevenueChartResponse;

import java.util.List;

import retrofit2.Call;

public class DashboardRepository {

    private DashboardService dashboardService;

    public DashboardRepository(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public Call<DashboardResponse> getDashboard(int shopId) {
        return dashboardService.getDashboard(shopId);
    }

    public Call<List<RevenueChartResponse>> getRevenueChart(int shopId, ChartType type) {
        return dashboardService.getRevenueChart(shopId, type);
    }

}
