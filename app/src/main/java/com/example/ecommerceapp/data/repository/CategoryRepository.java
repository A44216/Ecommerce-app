package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {

    private ApiService apiService;

    public CategoryRepository() {
        apiService = ApiClient.getApiService();
    }

    public Call<List<CategoryResponse>> getCategories() {
        return apiService.getCategories();
    }
}
