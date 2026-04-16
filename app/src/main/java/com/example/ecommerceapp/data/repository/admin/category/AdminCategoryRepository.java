package com.example.ecommerceapp.data.repository.admin.category;

import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.CategoryAdminResponse;

import java.util.List;

import retrofit2.Call;

public class AdminCategoryRepository {

    private final AdminCategoryService apiService;

    public AdminCategoryRepository(AdminCategoryService apiService) {
        this.apiService = apiService;
    }

    public Call<List<CategoryAdminResponse>> getAllCategories(Boolean isDeleted, String keyword) {
        return apiService.getAllCategories(isDeleted, keyword);
    }

    public Call<CategoryAdminResponse> createCategory(CategoryRequest request) {
        return apiService.createCategory(request);
    }

    public Call<CategoryAdminResponse> updateCategory(int id, CategoryRequest request) {
        return apiService.updateCategory(id, request);
    }

    public Call<Void> deleteCategory(int id) {
        return apiService.deleteCategory(id);
    }

    public Call<Void> restoreCategory(int id) {
        return apiService.restoreCategory(id);
    }
}