package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashboardService {

    @GET("seller/dashboard")
    Call<DashboardResponse> getDashboard(
            @Query("shopId") int shopId
    );
}