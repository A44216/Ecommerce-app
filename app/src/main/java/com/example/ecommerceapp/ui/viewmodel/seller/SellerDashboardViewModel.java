package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.enums.DateRange;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;
import com.example.ecommerceapp.data.repository.seller.SellerDashboardRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerDashboardViewModel extends ViewModel {

    private final SellerDashboardRepository repository;

    public SellerDashboardViewModel(SellerDashboardRepository repository) {
        this.repository = repository;
    }

    // KPI
    private final MutableLiveData<SellerDashboardKPIResponse> kpiData = new MutableLiveData<>();

    public LiveData<SellerDashboardKPIResponse> getKpiData() {
        return kpiData;
    }

    public void loadKpi(DateRange range) {
        repository.getKPI(range).enqueue(new Callback<SellerDashboardKPIResponse>() {
            @Override
            public void onResponse(Call<SellerDashboardKPIResponse> call,
                                   Response<SellerDashboardKPIResponse> response) {
                kpiData.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<SellerDashboardKPIResponse> call, Throwable t) {
                kpiData.setValue(null);
            }
        });
    }

    // TOP PRODUCTS
    private final MutableLiveData<SellerDashboardTopProductResponse> topProductData = new MutableLiveData<>();

    public LiveData<SellerDashboardTopProductResponse> getTopProductData() {
        return topProductData;
    }

    public void loadTopProducts(DateRange range) {
        repository.getTopProducts(range).enqueue(new Callback<SellerDashboardTopProductResponse>() {
            @Override
            public void onResponse(Call<SellerDashboardTopProductResponse> call,
                                   Response<SellerDashboardTopProductResponse> response) {
                topProductData.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<SellerDashboardTopProductResponse> call, Throwable t) {
                topProductData.setValue(null);
            }
        });
    }

    // CHART
    private final MutableLiveData<List<SellerRevenueChartResponse>> chartData = new MutableLiveData<>();

    public LiveData<List<SellerRevenueChartResponse>> getChartData() {
        return chartData;
    }

    public void loadChart(ChartType type) {
        repository.getRevenueChart(type).enqueue(new Callback<List<SellerRevenueChartResponse>>() {
            @Override
            public void onResponse(Call<List<SellerRevenueChartResponse>> call,
                                   Response<List<SellerRevenueChartResponse>> response) {
                chartData.setValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<List<SellerRevenueChartResponse>> call, Throwable t) {
                chartData.setValue(null);
            }
        });
    }
}