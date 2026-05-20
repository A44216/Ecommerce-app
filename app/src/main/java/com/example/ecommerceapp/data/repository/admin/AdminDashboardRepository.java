package com.example.ecommerceapp.data.repository.admin;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminDashboardService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardRepository {
    private final AdminDashboardService apiService;
    private final TokenManager tokenManager;

    public AdminDashboardRepository(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.apiService = ApiClient.getAdminDashboardService(tokenManager);
    }

    public void getKPI(String range, Callback<AdminDashboardKPIResponse> callback) {
        apiService.getKPI(range).enqueue(new Callback<AdminDashboardKPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardKPIResponse> call, @NonNull Response<AdminDashboardKPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(call, response);
                } else {
                    String errorMsg = "HTTP " + response.code() + " " + (response.message() != null ? response.message() : "");
                    Log.e("AdminDashboardRepo", "Error fetching KPI: " + errorMsg);
                    callback.onFailure(call, new Throwable(errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardKPIResponse> call, @NonNull Throwable t) {
                Log.e("AdminDashboardRepo", "Failure fetching KPI", t);
                callback.onFailure(call, t);
            }
        });
    }

    public void getRevenueChart(String type, Callback<List<com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse>> callback) {
        apiService.getRevenueChart(type).enqueue(callback);
    }

    public void getOrderStatusChart(String range, Callback<List<AdminOrderStatusChartResponse>> callback) {
        apiService.getOrderStatusChart(range).enqueue(callback);
    }

    public void getCategorySalesChart(String range, Callback<List<AdminCategorySalesChartResponse>> callback) {
        apiService.getCategorySalesChart(range).enqueue(callback);
    }

    public void getTopSellingShops(String range, Callback<List<AdminTopShopResponse>> callback) {
        apiService.getTopSellingShops(range).enqueue(callback);
    }

    public void getTopSellingProducts(String range, Callback<AdminDashboardTopProductResponse> callback) {
        apiService.getTopSellingProducts(range).enqueue(callback);
    }
}
