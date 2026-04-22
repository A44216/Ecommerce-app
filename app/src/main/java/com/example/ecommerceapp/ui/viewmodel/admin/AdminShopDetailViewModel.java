package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.admin.shop.AdminShopDetailResponse;
import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShopDetailViewModel extends ViewModel {

    private final AdminShopRepository repository;

    private final MutableLiveData<AdminShopDetailResponse> shopDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public AdminShopDetailViewModel(AdminShopRepository repository) {
        this.repository = repository;
    }

    public LiveData<AdminShopDetailResponse> getShopDetail() {
        return shopDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public void loadShopDetail(int shopId) {
        isLoading.setValue(true);
        repository.getShopById(shopId).enqueue(new Callback<AdminShopDetailResponse>() {
            @Override
            public void onResponse(Call<AdminShopDetailResponse> call, Response<AdminShopDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    shopDetail.setValue(response.body());
                } else {
                    error.setValue("Lỗi tải thông tin chi tiết: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AdminShopDetailResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void updateShopStatus(int shopId, ShopStatus newStatus) {
        isLoading.setValue(true);
        repository.updateShopStatus(shopId, newStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    updateSuccess.setValue(true);
                    AdminShopDetailResponse currentDetail = shopDetail.getValue();
                    if (currentDetail != null) {
                        currentDetail.setStatus(newStatus);
                        shopDetail.setValue(currentDetail); // Trigger update UI
                    }
                } else {
                    error.setValue("Cập nhật trạng thái thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
