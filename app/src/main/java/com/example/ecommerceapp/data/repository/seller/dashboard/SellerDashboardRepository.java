package com.example.ecommerceapp.data.repository.seller.dashboard;

import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;

public class SellerDashboardRepository {

    private SellerDashboardService dashboardService;

    public SellerDashboardRepository(SellerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public Call<SellerDashboardResponse> getDashboard() {
        return dashboardService.getDashboard();
    }

    public Call<List<SellerRevenueChartResponse>> getRevenueChart(ChartType type) {
        return dashboardService.getRevenueChart(type);
    }

}
