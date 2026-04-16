package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SellerDashboardService {

    // KPI
    @GET("seller/dashboard/kpi")
    Call<SellerDashboardKPIResponse> getKPI(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // TOP PRODUCTS
    @GET("seller/dashboard/top-products")
    Call<SellerDashboardTopProductResponse> getTopProducts(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // REVENUE CHART
    @GET("seller/dashboard/revenue-chart")
    Call<List<SellerRevenueChartResponse>> getRevenueChart(
            @Query("type") ChartType type
    );
}