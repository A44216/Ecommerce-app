package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface AdminDashboardService {

    @GET("api/admin/dashboard/kpi")
    Call<AdminDashboardKPIResponse> getKPI(
            @Header("Authorization") String token,
            @Query("range") String range
    );

    @GET("api/admin/dashboard/revenue-chart")
    Call<List<com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse>> getRevenueChart(
            @Header("Authorization") String token,
            @Query("type") String type
    );

    @GET("api/admin/dashboard/order-status-chart")
    Call<List<AdminOrderStatusChartResponse>> getOrderStatusChart(
            @Header("Authorization") String token,
            @Query("range") String range
    );

    @GET("api/admin/dashboard/category-sales-chart")
    Call<List<AdminCategorySalesChartResponse>> getCategorySalesChart(
            @Header("Authorization") String token,
            @Query("range") String range
    );

    @GET("api/admin/dashboard/top-selling-shops")
    Call<List<AdminTopShopResponse>> getTopSellingShops(
            @Header("Authorization") String token,
            @Query("range") String range
    );

    @GET("api/admin/dashboard/top-selling-products")
    Call<AdminDashboardTopProductResponse> getTopSellingProducts(
            @Header("Authorization") String token,
            @Query("range") String range
    );
}
