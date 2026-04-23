package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.admin.profile.AdminProfileResponse;
import com.example.ecommerceapp.data.repository.admin.AdminProfileRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProfileViewModel extends ViewModel {

    private final AdminProfileRepository repository;

    private final MutableLiveData<AdminProfileResponse> profile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public LiveData<AdminProfileResponse> getProfileData() {
        return profile;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public AdminProfileViewModel(AdminProfileRepository repo) {
        this.repository = repo;
    }

    // GET
    public void loadProfile() {

        loading.setValue(true);

        repository.getProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AdminProfileResponse> call,
                                   Response<AdminProfileResponse> response) {

                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    profile.setValue(response.body());
                } else {
                    error.setValue("Load profile failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AdminProfileResponse> call, Throwable t) {
                loading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    // UPDATE
    public void updateProfile(String fullName, String email, String phone, String avatar) {

        loading.setValue(true);

        repository.updateProfile(new com.example.ecommerceapp.data.model.request.admin.profile.AdminProfileRequest(fullName, email, phone, avatar))
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(Call<AdminProfileResponse> call, Response<AdminProfileResponse> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            profile.setValue(response.body());
                            updateSuccess.setValue(true);
                        } else {
                            error.setValue("Update failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminProfileResponse> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }
}
