package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SellerDashboardService {

    @GET("seller/dashboard")
    Call<SellerDashboardResponse> getDashboard();

    @GET("seller/dashboard/revenue-chart")
    Call<List<SellerRevenueChartResponse>> getRevenueChart(
            @Query("type") ChartType type
    );

}