package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.repository.DashboardRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerDashboardViewModel extends ViewModel {

    private final DashboardRepository repository;

    public SellerDashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
    }

    private final MutableLiveData<SellerDashboardResponse> dashboardData = new MutableLiveData<>();

    public LiveData<SellerDashboardResponse> getDashboardData() {
        return dashboardData;
    }

    public void loadDashboard(int shopId) {
        repository.getDashboard(shopId).enqueue(new Callback<SellerDashboardResponse>() {
            @Override
            public void onResponse(Call<SellerDashboardResponse> call, Response<SellerDashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dashboardData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerDashboardResponse> call, Throwable t) {
                dashboardData.setValue(null);
            }
        });
    }
}