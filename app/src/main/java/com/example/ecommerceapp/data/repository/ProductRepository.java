package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.data.model.response.ProductResponse;

import java.util.List;

import retrofit2.Call;

public class ProductRepository {

    private final ProductService apiService;

    public ProductRepository(ProductService apiService) {
        this.apiService = apiService;
    }

    public Call<List<ProductResponse>> getProducts() {
        return apiService.getProducts();
    }

    public Call<ProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    public Call<List<ProductResponse>> getProductsByShop(int shopId) {
        return apiService.getProductsByShop(shopId);
    }

    public Call<Void> deleteProduct(int id) {
        return apiService.deleteProduct(id);
    }
}
