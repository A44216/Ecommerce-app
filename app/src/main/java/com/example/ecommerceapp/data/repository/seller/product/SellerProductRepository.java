package com.example.ecommerceapp.data.repository.seller.product;

import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.data.model.request.seller.product.SellerProductRequest;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;

import java.util.List;

import retrofit2.Call;

public class SellerProductRepository {

    private final SellerProductService apiService;

    public SellerProductRepository(SellerProductService apiService) {
        this.apiService = apiService;
    }

    public Call<List<SellerProductResponse>> getProducts() {
        return apiService.getProducts();
    }

    public Call<SellerProductResponse> getProductById(int id) {
        return apiService.getProductById(id);
    }

    public Call<List<SellerProductResponse>> getProductsByShop(int shopId) {
        return apiService.getProductsByShop(shopId);
    }

    public Call<SellerProductResponse> createProduct(SellerProductRequest request) {
        return apiService.createProduct(request);
    }

    public Call<SellerProductResponse> updateProduct(int id, SellerProductRequest request) {
        return apiService.updateProduct(id, request);
    }

    public Call<Void> deleteProduct(int id) {
        return apiService.deleteProduct(id);
    }

    public Call<List<SellerProductResponse>> search(String keyword, Integer shopId) {
        return apiService.searchProducts(keyword, shopId);
    }

}
