package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminDashboardTopProductResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopShopResponse;
import com.example.ecommerceapp.data.repository.admin.AdminDashboardRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardViewModel extends ViewModel {

    private final AdminDashboardRepository repository;

    private final MutableLiveData<AdminDashboardKPIResponse> kpiLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminRevenueChartResponse>> revenueChartLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminOrderStatusChartResponse>> orderStatusChartLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminCategorySalesChartResponse>> categorySalesChartLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminTopShopResponse>> topShopsLiveData = new MutableLiveData<>();
    private final MutableLiveData<AdminDashboardTopProductResponse> topProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> topProductTypeLiveData = new MutableLiveData<>("SOLD");

    public AdminDashboardViewModel(AdminDashboardRepository repository) {
        this.repository = repository;
    }

    public LiveData<AdminDashboardKPIResponse> getKpiLiveData() { return kpiLiveData; }
    public LiveData<List<AdminRevenueChartResponse>> getRevenueChartLiveData() { return revenueChartLiveData; }
    public LiveData<List<AdminOrderStatusChartResponse>> getOrderStatusChartLiveData() { return orderStatusChartLiveData; }
    public LiveData<List<AdminCategorySalesChartResponse>> getCategorySalesChartLiveData() { return categorySalesChartLiveData; }
    public LiveData<List<AdminTopShopResponse>> getTopShopsLiveData() { return topShopsLiveData; }
    public LiveData<AdminDashboardTopProductResponse> getTopProductsLiveData() { return topProductsLiveData; }
    public LiveData<String> getTopProductTypeLiveData() { return topProductTypeLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // States
    private String currentGlobalDateRange = "TODAY";
    private String currentChartType = "DAY";
    private String currentTopProductType = "SOLD";

    public String getCurrentGlobalDateRange() { return currentGlobalDateRange; }
    public String getCurrentChartType() { return currentChartType; }
    public String getCurrentTopProductType() { return currentTopProductType; }

    public void setGlobalDateRange(String range) {
        if (!range.equals(this.currentGlobalDateRange)) {
            this.currentGlobalDateRange = range;
            fetchAllDataWithCurrentRange();
        }
    }

    public void setChartType(String type) {
        if (!type.equals(this.currentChartType)) {
            this.currentChartType = type;
            fetchRevenueChart();
        }
    }

    public void setTopProductType(String type) {
        if (!type.equals(this.currentTopProductType)) {
            this.currentTopProductType = type;
            topProductTypeLiveData.setValue(type);
        }
    }

    public void fetchAllDataWithCurrentRange() {
        isLoading.setValue(true);
        fetchKPI();
        fetchRevenueChart();
        fetchOrderStatusChart();
        fetchCategorySalesChart();
        fetchTopShops();
        fetchTopProducts();
    }

    public void fetchRevenueChart() {
        repository.getRevenueChart(currentChartType, new Callback<List<AdminRevenueChartResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminRevenueChartResponse>> call, @NonNull Response<List<AdminRevenueChartResponse>> response) {
                if (response.isSuccessful()) revenueChartLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<AdminRevenueChartResponse>> call, @NonNull Throwable t) {}
        });
    }

    public void fetchKPI() {
        repository.getKPI(currentGlobalDateRange, new Callback<AdminDashboardKPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardKPIResponse> call, @NonNull Response<AdminDashboardKPIResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    kpiLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Lỗi tải KPI");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminDashboardKPIResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void fetchOrderStatusChart() {
        repository.getOrderStatusChart(currentGlobalDateRange, new Callback<List<AdminOrderStatusChartResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminOrderStatusChartResponse>> call, @NonNull Response<List<AdminOrderStatusChartResponse>> response) {
                if (response.isSuccessful()) orderStatusChartLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<AdminOrderStatusChartResponse>> call, @NonNull Throwable t) {}
        });
    }

    public void fetchCategorySalesChart() {
        repository.getCategorySalesChart(currentGlobalDateRange, new Callback<List<AdminCategorySalesChartResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminCategorySalesChartResponse>> call, @NonNull Response<List<AdminCategorySalesChartResponse>> response) {
                if (response.isSuccessful()) categorySalesChartLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<AdminCategorySalesChartResponse>> call, @NonNull Throwable t) {}
        });
    }

    public void fetchTopShops() {
        repository.getTopSellingShops(currentGlobalDateRange, new Callback<List<AdminTopShopResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminTopShopResponse>> call, @NonNull Response<List<AdminTopShopResponse>> response) {
                if (response.isSuccessful()) topShopsLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<AdminTopShopResponse>> call, @NonNull Throwable t) {}
        });
    }

    public void fetchTopProducts() {
        repository.getTopSellingProducts(currentGlobalDateRange, new Callback<AdminDashboardTopProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminDashboardTopProductResponse> call, @NonNull Response<AdminDashboardTopProductResponse> response) {
                if (response.isSuccessful()) topProductsLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<AdminDashboardTopProductResponse> call, @NonNull Throwable t) {}
        });
    }
}
