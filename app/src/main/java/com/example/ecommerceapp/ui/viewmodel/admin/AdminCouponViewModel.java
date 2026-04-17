package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.admin.profile.AdminCouponRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.repository.admin.profile.AdminCouponRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCouponViewModel extends ViewModel {

    private final AdminCouponRepository repository;

    public AdminCouponViewModel(AdminCouponRepository repository) {
        this.repository = repository;
    }

    // LIST
    private final MutableLiveData<PageResponse<AdminCouponResponse>> couponsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<PageResponse<AdminCouponResponse>> getCouponsLiveData() {
        return couponsLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void getCoupons(int page, int size, String status, String keyword) {
        loading.setValue(true);

        repository.getCoupons(page, size, status, keyword)
                .enqueue(new Callback<PageResponse<AdminCouponResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminCouponResponse>> call,
                                           Response<PageResponse<AdminCouponResponse>> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            couponsLiveData.setValue(response.body());
                        } else {
                            error.setValue("Failed to load coupons");
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminCouponResponse>> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // CREATE
    private final MutableLiveData<AdminCouponResponse> createResult = new MutableLiveData<>();

    public LiveData<AdminCouponResponse> getCreateResult() {
        return createResult;
    }

    public void createCoupon(AdminCouponRequest request) {
        loading.setValue(true);

        repository.createCoupon(request)
                .enqueue(new Callback<AdminCouponResponse>() {
                    @Override
                    public void onResponse(Call<AdminCouponResponse> call,
                                           Response<AdminCouponResponse> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            createResult.setValue(response.body());
                        } else {
                            error.setValue("Create failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminCouponResponse> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // DELETE
    public void deleteCoupon(Integer id) {
        loading.setValue(true);

        repository.deleteCoupon(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (!response.isSuccessful()) {
                            error.setValue("Delete failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // ENABLE
    public void enableCoupon(Integer id) {
        repository.enableCoupon(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    // DISABLE
    public void disableCoupon(Integer id) {
        repository.disableCoupon(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
}