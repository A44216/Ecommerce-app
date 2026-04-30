package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.seller.SellerCategoryService;
import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {

    private CategoryService publicApiService;
    private SellerCategoryService sellerApiService;

    public CategoryRepository(CategoryService apiService) {
        this.publicApiService = apiService;
    }

    public CategoryRepository(SellerCategoryService apiService) {
        this.sellerApiService = apiService;
    }

    // USER - PUBLIC
    public Call<List<CategoryResponse>> getPublicCategories() {
        return publicApiService.getCategories();
    }

    // SELLER - GET ALL
    public Call<List<CategoryResponse>> getCategories() {
        return sellerApiService.getCategories(null);
    }

    // SELLER - SEARCH
    public Call<List<CategoryResponse>> searchCategories(String keyword) {
        return sellerApiService.getCategories(keyword);
    }

    // SELLER - GET BY ID
    public Call<CategoryResponse> getCategoryById(int id) {
        return sellerApiService.getCategoryById(id);
    }
}
