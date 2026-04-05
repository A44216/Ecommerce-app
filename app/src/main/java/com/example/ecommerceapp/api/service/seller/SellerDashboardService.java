package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SellerDashboardService {

    @GET("seller/dashboard")
    Call<SellerDashboardResponse> getDashboard(
            @Query("shopId") int shopId
    );

    @GET("seller/dashboard/revenue-chart")
    Call<List<SellerRevenueChartResponse>> getRevenueChart(
            @Query("shopId") int shopId,
            @Query("type") ChartType type
    );

}