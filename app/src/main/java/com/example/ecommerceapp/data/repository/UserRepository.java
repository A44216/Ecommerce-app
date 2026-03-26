package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserResponse>> getUsers() {
        return apiService.getUsers();
    }
}