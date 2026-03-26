package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.response.LoginResponse;

import retrofit2.Call;

public class LoginRepository {

    private final ApiService apiService;

    public LoginRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return apiService.loginUser(request);
    }
}
