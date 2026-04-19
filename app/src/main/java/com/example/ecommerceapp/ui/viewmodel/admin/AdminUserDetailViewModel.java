package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserDetailResponse;
import com.example.ecommerceapp.data.repository.admin.user.AdminUserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDetailViewModel extends ViewModel {

    private final AdminUserRepository repository;

    private final MutableLiveData<AdminUserDetailResponse> userDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> statusChangeSuccess = new MutableLiveData<>();

    public AdminUserDetailViewModel(AdminUserRepository repository) {
        this.repository = repository;
    }

    public LiveData<AdminUserDetailResponse> getUserDetailLiveData() {
        return userDetailLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getStatusChangeSuccess() {
        return statusChangeSuccess;
    }

    public void getUserDetail(int userId) {
        loading.setValue(true);
        repository.getUserById(userId).enqueue(new Callback<AdminUserDetailResponse>() {
            @Override
            public void onResponse(Call<AdminUserDetailResponse> call, Response<AdminUserDetailResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userDetailLiveData.setValue(response.body());
                } else {
                    error.setValue("Không thể tải thông tin người dùng");
                }
            }

            @Override
            public void onFailure(Call<AdminUserDetailResponse> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    public void changeStatus(int userId, UserStatus newStatus) {
        loading.setValue(true);
        repository.changeStatus(userId, newStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    statusChangeSuccess.setValue(true);
                    // Reload data
                    getUserDetail(userId);
                } else {
                    statusChangeSuccess.setValue(false);
                    error.setValue("Không thể thay đổi trạng thái");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.setValue(false);
                statusChangeSuccess.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
}
