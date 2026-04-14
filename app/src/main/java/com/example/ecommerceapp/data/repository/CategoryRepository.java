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

    // CREATE
    public Call<CategoryResponse> createCategory(CategoryRequest request) {
        return apiService.createCategory(request);
    }

    // UPDATE
    public Call<CategoryResponse> updateCategory(int id, CategoryRequest request) {
        return apiService.updateCategory(id, request);
    }

    // DELETE
    public Call<Void> deleteCategory(int id) {
        return apiService.deleteCategory(id);
    }

    // GET BY ID
    public Call<CategoryResponse> getCategoryById(int id) {
        return apiService.getCategoryById(id);
    }

}
