package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.repository.admin.user.AdminUserRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserViewModel extends ViewModel {

    private final AdminUserRepository repository;

    private String currentKeyword = "";
    private UserStatus currentStatus = null;
    private boolean isDataLoaded = false;

    public AdminUserViewModel(AdminUserRepository repository) {
        this.repository = repository;
    }

    public String getCurrentKeyword() { return currentKeyword; }
    public void setCurrentKeyword(String keyword) { this.currentKeyword = keyword; }

    public UserStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(UserStatus status) { this.currentStatus = status; }

    public boolean isDataLoaded() { return isDataLoaded; }

    private final MutableLiveData<List<AdminUserResponse>> customersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminUserResponse>> sellersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<AdminUserResponse>> getCustomersLiveData() {
        return customersLiveData;
    }

    public LiveData<List<AdminUserResponse>> getSellersLiveData() {
        return sellersLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private int pendingRequests = 0;

    public void getUsers(int page, int size, UserStatus status, String keyword, boolean isSilent) {
        if (!isSilent) loading.setValue(true);
        pendingRequests = 2;
        isDataLoaded = true;

        repository.getUsers(page, size, Role.CUSTOMER, status, keyword)
                .enqueue(new Callback<PageResponse<AdminUserResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminUserResponse>> call, Response<PageResponse<AdminUserResponse>> response) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            customersLiveData.setValue(response.body().getItems());
                        } else {
                            error.setValue("Failed to load customers");
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminUserResponse>> call, Throwable t) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });

        repository.getUsers(page, size, Role.SELLER, status, keyword)
                .enqueue(new Callback<PageResponse<AdminUserResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminUserResponse>> call, Response<PageResponse<AdminUserResponse>> response) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            sellersLiveData.setValue(response.body().getItems());
                        } else {
                            error.setValue("Failed to load sellers");
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminUserResponse>> call, Throwable t) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }
}
