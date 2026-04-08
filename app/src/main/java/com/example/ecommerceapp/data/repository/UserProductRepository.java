package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import java.util.List;
import retrofit2.Call;

public class UserProductRepository {
    private final UserProductService apiService;

    public UserProductRepository(UserProductService apiService) {
        this.apiService = apiService;
    }

    public Call<List<UserProductResponse>> getProducts() {
        return apiService.getProducts();
    }

    public Call<UserProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    public Call<List<UserProductResponse>> getProductsByCategory(int categoryId) {
        return apiService.getProductsByCategory(categoryId);
    }

    public Call<List<UserProductResponse>> searchProducts(String keyword, Integer shopId) {
        return apiService.searchProducts(keyword, shopId);
    }
}