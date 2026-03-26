package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.ProductResponse;

import java.util.List;

import retrofit2.Call;

public class ProductRepository {

    private final ApiService apiService;

    public ProductRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<ProductResponse>> getProducts() {
        return apiService.getProducts();
    }
}
