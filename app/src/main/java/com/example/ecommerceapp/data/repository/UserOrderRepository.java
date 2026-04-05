package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import retrofit2.Call;

public class UserOrderRepository {
    private final UserOrderApiService apiService;

    public UserOrderRepository(UserOrderApiService apiService) {
        this.apiService = apiService;
    }

    public Call<Void> createOrder(UserOrderRequest request) {
        return apiService.createOrder(request);
    }
}