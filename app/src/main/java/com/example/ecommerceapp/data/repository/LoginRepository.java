package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.response.LoginResponse;


import retrofit2.Call;

public class LoginRepository {

    private final AuthService apiService;

    public LoginRepository(AuthService apiService) {
        this.apiService = apiService;
    }

    public Call<LoginResponse> login(LoginRequest request) {
        return apiService.loginUser(request);
    }
}
