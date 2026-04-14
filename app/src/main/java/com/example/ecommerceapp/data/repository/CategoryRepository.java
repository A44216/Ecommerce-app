package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {

    private final CategoryService apiService;

    public CategoryRepository(CategoryService apiService) {
        this.apiService = apiService;
    }

    // GET ALL
    public Call<List<CategoryResponse>> getCategories() {
        return apiService.getCategories(null);
    }

    // SEARCH
    public Call<List<CategoryResponse>> searchCategories(String keyword) {
        return apiService.getCategories(keyword);
    }

    // GET BY ID
    public Call<CategoryResponse> getCategoryById(int id) {
        return apiService.getCategoryById(id);
    }
}
