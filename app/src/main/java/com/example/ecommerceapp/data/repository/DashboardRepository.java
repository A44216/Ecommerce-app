package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;

public class DashboardRepository {

    private SellerDashboardService dashboardService;

    public DashboardRepository(SellerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public Call<SellerDashboardResponse> getDashboard(int shopId) {
        return dashboardService.getDashboard(shopId);
    }

    public Call<List<SellerRevenueChartResponse>> getRevenueChart(int shopId, ChartType type) {
        return dashboardService.getRevenueChart(shopId, type);
    }

}
