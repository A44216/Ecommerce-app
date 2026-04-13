package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;

public class UserRepository {

    private final UserService apiService;

    public UserRepository(UserService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserResponse>> getUsers() {
        return apiService.getUsers();
    }
}