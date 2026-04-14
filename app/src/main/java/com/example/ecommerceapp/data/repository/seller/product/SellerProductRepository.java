package com.example.ecommerceapp.data.repository.seller.product;

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
            String keyword,
            int page,
            int size
    ) {
        return apiService.getProducts(status, keyword, page, size);
    }

    // GET DETAIL
    public Call<SellerProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    // GET DELETED
    public Call<PageResponse<SellerProductResponse>> getDeletedProducts(int page, int size) {
        return apiService.getDeletedProducts(page, size);
    }

    // DELETE
    public Call<Void> deleteProduct(int id) {
        return apiService.deleteProduct(id);
    }

    // CREATE
    public Call<SellerProductResponse> createProduct(SellerProductRequest request) {
        return apiService.createProduct(request);
    }

    // UPDATE
    public Call<SellerProductResponse> updateProduct(int id, SellerProductRequest request) {
        return apiService.updateProduct(id, request);
    }
}