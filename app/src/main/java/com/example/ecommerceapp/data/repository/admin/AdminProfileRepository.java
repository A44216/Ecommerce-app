package com.example.ecommerceapp.data.repository.admin;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminProfileService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.profile.AdminProfileRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminProfileResponse;

import retrofit2.Call;

public class AdminProfileRepository {

    private final AdminProfileService api;

    public AdminProfileRepository(TokenManager tm) {
        api = ApiClient.getAdminProfileService(tm);
    }

    public Call<AdminProfileResponse> getProfile() {
        return api.getProfile();
    }

    public Call<AdminProfileResponse> updateProfile(AdminProfileRequest request) {
        return api.updateProfile(request);
    }
}