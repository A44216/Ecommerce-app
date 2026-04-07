package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserCategoryApiService;
import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import java.util.List;
import retrofit2.Call;

public class UserCategoryRepository {
    private final UserCategoryApiService apiService;

    public UserCategoryRepository(UserCategoryApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserCategoryResponse>> getAllCategories() {
        return apiService.getAllCategories();
    }
}