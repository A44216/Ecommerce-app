package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AdminDashboardService {

    @GET("admin/dashboard/kpi")
    Call<AdminDashboardKPIResponse> getKPI(
            @Query("range") String range
    );

    @GET("admin/dashboard/revenue-chart")
    Call<List<com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse>> getRevenueChart(
            @Query("type") String type
    );

    @GET("admin/dashboard/order-status-chart")
    Call<List<AdminOrderStatusChartResponse>> getOrderStatusChart(
            @Query("range") String range
    );

    @GET("admin/dashboard/category-sales-chart")
    Call<List<AdminCategorySalesChartResponse>> getCategorySalesChart(
            @Query("range") String range
    );

    @GET("admin/dashboard/top-selling-shops")
    Call<List<AdminTopShopResponse>> getTopSellingShops(
            @Query("range") String range
    );

    @GET("admin/dashboard/top-selling-products")
    Call<AdminDashboardTopProductResponse> getTopSellingProducts(
            @Query("range") String range
    );
}
