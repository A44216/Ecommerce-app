package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;
import com.example.ecommerceapp.data.model.response.dashboard.RevenueChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashboardService {

    @GET("seller/dashboard")
    Call<DashboardResponse> getDashboard(
            @Query("shopId") int shopId
    );

    @GET("seller/dashboard/revenue-chart")
    Call<List<RevenueChartResponse>> getRevenueChart(
            @Query("shopId") int shopId,
            @Query("type") ChartType type
    );

}