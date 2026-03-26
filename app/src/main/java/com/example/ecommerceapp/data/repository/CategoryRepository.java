package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {

    private final ApiService apiService;

    public CategoryRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<CategoryResponse>> getCategories() {
        return apiService.getCategories();
    }
}
