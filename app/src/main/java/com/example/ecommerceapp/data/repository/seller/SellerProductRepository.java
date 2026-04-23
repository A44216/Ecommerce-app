package com.example.ecommerceapp.data.repository.seller;

import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.data.model.request.seller.product.SellerProductRequest;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;

import retrofit2.Call;

public class SellerProductRepository {

    private final SellerProductService apiService;

    public SellerProductRepository(SellerProductService apiService) {
        this.apiService = apiService;
    }

    public Call<PageResponse<SellerProductResponse>> getProducts(
            String status,
            Boolean isDeleted,
            String keyword,
            int page,
            int size
    ) {
        return apiService.getProducts(status, isDeleted, keyword, page, size);
    }

    public Call<SellerProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    public Call<Void> deleteProduct(int id) {
        return apiService.deleteProduct(id);
    }

    public Call<SellerProductResponse> createProduct(SellerProductRequest request) {
        return apiService.createProduct(request);
    }

    public Call<SellerProductResponse> updateProduct(int id, SellerProductRequest request) {
        return apiService.updateProduct(id, request);
    }

    public Call<Void> restoreProduct(int id) {
        return apiService.restoreProduct(id);
    }

    public Call<Void> submitProduct(int id) {
        return apiService.submitProduct(id);
    }
}