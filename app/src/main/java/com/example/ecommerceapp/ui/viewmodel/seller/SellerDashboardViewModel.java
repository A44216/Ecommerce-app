package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerRevenueChartResponse;
import com.example.ecommerceapp.data.repository.DashboardRepository;

import java.util.List;

public class SellerDashboardViewModel extends ViewModel {

    private final DashboardRepository repository;

    public SellerDashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
    }

    // DASHBOARD
    public LiveData<SellerDashboardResponse> getDashboard(int shopId) {
        return repository.getDashboard(shopId);
    }

    // CHART
    public LiveData<List<SellerRevenueChartResponse>> getRevenueChart(int shopId, ChartType type) {
        return repository.getRevenueChart(shopId, type);
    }
}