package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;

import java.util.List;

import retrofit2.Call;

public class UserOrderRepository {
    private final UserOrderApiService apiService;

    public UserOrderRepository(UserOrderApiService apiService) {
        this.apiService = apiService;
    }

    public Call<Void> createOrder(UserOrderRequest request) {
        return apiService.createOrder(request);
    }

    public Call<List<UserOrderResponse>> getOrdersByUser(int userId) {
        return apiService.getOrdersByUser(userId);
    }
}