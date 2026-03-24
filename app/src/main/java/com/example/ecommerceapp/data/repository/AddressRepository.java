package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.AddressResponse;

import java.util.List;

import retrofit2.Call;
public class AddressRepository {

    private ApiService apiService;

    public AddressRepository() {
        apiService = ApiClient.getApiService();
    }

    public Call<List<AddressResponse>> getAddresses() {
        return apiService.getAddresses();
    }
}
