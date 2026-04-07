package com.example.ecommerceapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerRevenueChartResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {

    private final SellerDashboardService dashboardService;

    public DashboardRepository(SellerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ================= DASHBOARD =================
    public LiveData<SellerDashboardResponse> getDashboard(int shopId) {

        MutableLiveData<SellerDashboardResponse> data = new MutableLiveData<>();

        dashboardService.getDashboard(shopId)
                .enqueue(new Callback<SellerDashboardResponse>() {
                    @Override
                    public void onResponse(Call<SellerDashboardResponse> call,
                                           Response<SellerDashboardResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<SellerDashboardResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }

    // ================= REVENUE CHART =================
    public LiveData<List<SellerRevenueChartResponse>> getRevenueChart(int shopId, ChartType type) {

        MutableLiveData<List<SellerRevenueChartResponse>> data = new MutableLiveData<>();

        dashboardService.getRevenueChart(shopId, type)
                .enqueue(new Callback<List<SellerRevenueChartResponse>>() {
                    @Override
                    public void onResponse(Call<List<SellerRevenueChartResponse>> call,
                                           Response<List<SellerRevenueChartResponse>> response) {

                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SellerRevenueChartResponse>> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }
}