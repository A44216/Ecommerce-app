package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.data.model.request.UserAddressRequest;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;

import java.util.List;

import retrofit2.Call;

public class UserAddressRepository {
    private final UserAddressApiService apiService;

    public UserAddressRepository(UserAddressApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserAddressResponse>> getAddressesByUserId(int userId) {
        return apiService.getAddressesByUserId(userId);
    }

    // hàm gửi Request
    public Call<UserAddressResponse> createAddress(UserAddressRequest request) {
        return apiService.createAddress(request);
    }
}