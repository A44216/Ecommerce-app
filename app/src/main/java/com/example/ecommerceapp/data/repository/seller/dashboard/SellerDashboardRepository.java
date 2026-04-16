package com.example.ecommerceapp.data.repository.seller.dashboard;

import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;

public class SellerDashboardRepository {

    private final SellerDashboardService dashboardService;

    public SellerDashboardRepository(SellerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // KPI
    public Call<SellerDashboardKPIResponse> getKPI(String startDate, String endDate) {
        return dashboardService.getKPI(startDate, endDate);
    }

    // TOP PRODUCTS
    public Call<SellerDashboardTopProductResponse> getTopProducts(String startDate, String endDate) {
        return dashboardService.getTopProducts(startDate, endDate);
    }

    // REVENUE CHART
    public Call<List<SellerRevenueChartResponse>> getRevenueChart(ChartType type) {
        return dashboardService.getRevenueChart(type);
    }
}