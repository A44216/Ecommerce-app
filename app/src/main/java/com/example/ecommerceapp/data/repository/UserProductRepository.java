package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.data.model.response.UserPageResponse;
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

    public Call<UserPageResponse<UserProductResponse>> getProductsPaginated(int page, int size, String sortBy) {
        return apiService.getProductsPaginated(page, size, sortBy);
    }

    public Call<UserProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    public Call<List<UserProductResponse>> getProductsByCategory(int categoryId) {
        return apiService.getProductsByCategory(categoryId);
    }

    public Call<List<String>> suggestProducts(String keyword) {
        return apiService.suggestProducts(keyword);
    }

    public Call<List<UserProductResponse>> getTrendingProducts() {
        return apiService.getTrendingProducts();
    }

    public Call<List<UserProductResponse>> searchProducts(String keyword, Integer shopId) {
        return apiService.searchProducts(keyword, shopId);
    }

    public Call<UserPageResponse<UserProductResponse>> searchProductsPaginated(String keyword, int page, int size, String sortBy) {
        return apiService.searchProductsPaginated(keyword, page, size, sortBy);
    }

    public Call<UserPageResponse<UserProductResponse>> getProductsByCategoryPaginated(int categoryId, int page, int size, String sortBy) {
        return apiService.getProductsByCategoryPaginated(categoryId, page, size, sortBy);
    }
}