package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;
import com.example.ecommerceapp.data.repository.DashboardRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;

    public DashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
    }

    private final MutableLiveData<DashboardResponse> dashboardData = new MutableLiveData<>();

    public LiveData<DashboardResponse> getDashboardData() {
        return dashboardData;
    }

    public void loadDashboard(int shopId) {
        repository.getDashboard(shopId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dashboardData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                dashboardData.setValue(null);
            }
        });
    }
}