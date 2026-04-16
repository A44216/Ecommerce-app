package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;
import com.example.ecommerceapp.data.repository.seller.dashboard.SellerDashboardRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerDashboardViewModel extends ViewModel {

    private final SellerDashboardRepository repository;

    public SellerDashboardViewModel(SellerDashboardRepository repository) {
        this.repository = repository;
    }

    // ===== KPI =====
    private final MutableLiveData<SellerDashboardKPIResponse> kpiData = new MutableLiveData<>();

    public LiveData<SellerDashboardKPIResponse> getKpiData() {
        return kpiData;
    }

    // ===== TOP PRODUCTS =====
    private final MutableLiveData<SellerDashboardTopProductResponse> topProductData = new MutableLiveData<>();

    public LiveData<SellerDashboardTopProductResponse> getTopProductData() {
        return topProductData;
    }

    // ===== CHART =====
    private final MutableLiveData<List<SellerRevenueChartResponse>> chartData = new MutableLiveData<>();

    public LiveData<List<SellerRevenueChartResponse>> getChartData() {
        return chartData;
    }

    // ===== LOAD ALL =====
    public void loadDashboard(String startDate, String endDate, ChartType type) {

        // KPI
        repository.getKPI(startDate, endDate).enqueue(new Callback<SellerDashboardKPIResponse>() {
            @Override
            public void onResponse(Call<SellerDashboardKPIResponse> call, Response<SellerDashboardKPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kpiData.setValue(response.body());
                } else {
                    kpiData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SellerDashboardKPIResponse> call, Throwable t) {
                kpiData.setValue(null);
            }
        });

        // TOP PRODUCTS
        repository.getTopProducts(startDate, endDate).enqueue(new Callback<SellerDashboardTopProductResponse>() {
            @Override
            public void onResponse(Call<SellerDashboardTopProductResponse> call, Response<SellerDashboardTopProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topProductData.setValue(response.body());
                } else {
                    topProductData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SellerDashboardTopProductResponse> call, Throwable t) {
                topProductData.setValue(null);
            }
        });

        // CHART
        repository.getRevenueChart(type).enqueue(new Callback<List<SellerRevenueChartResponse>>() {
            @Override
            public void onResponse(Call<List<SellerRevenueChartResponse>> call, Response<List<SellerRevenueChartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chartData.setValue(response.body());
                } else {
                    chartData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SellerRevenueChartResponse>> call, Throwable t) {
                chartData.setValue(null);
            }
        });
    }
}