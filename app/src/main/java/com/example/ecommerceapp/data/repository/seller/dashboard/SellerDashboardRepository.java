package com.example.ecommerceapp.data.repository.seller.dashboard;

import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.enums.DateRange;
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
    public Call<SellerDashboardKPIResponse> getKPI(DateRange range) {
        return dashboardService.getKPI(range);
    }

    // TOP PRODUCTS
    public Call<SellerDashboardTopProductResponse> getTopProducts(DateRange range) {
        return dashboardService.getTopProducts(range);
    }

    // REVENUE CHART
    public Call<List<SellerRevenueChartResponse>> getRevenueChart(ChartType type) {
        return dashboardService.getRevenueChart(type);
    }
}