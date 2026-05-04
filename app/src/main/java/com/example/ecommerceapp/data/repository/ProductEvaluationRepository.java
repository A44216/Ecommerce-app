package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.ProductEvaluationService;
import com.example.ecommerceapp.data.model.request.ProductEvaluationRequest;
import com.example.ecommerceapp.data.model.response.ProductEvaluationResponse;

import java.util.List;

import retrofit2.Call;

public class ProductEvaluationRepository {

    private final ProductEvaluationService apiService;

    public ProductEvaluationRepository(ProductEvaluationService apiService) {
        this.apiService = apiService;
    }

    // Top Deals (Fuzzy)
    public Call<List<ProductEvaluationResponse>> getTopDeals(int limit) {
        return apiService.getTopDeals(limit);
    }

    // Trending
    public Call<List<ProductEvaluationResponse>> getTrendingDeals(int limit) {
        return apiService.getTrendingDeals(limit);
    }

    // Evaluate 1 product
    public Call<ProductEvaluationResponse> evaluateProduct(ProductEvaluationRequest request) {
        return apiService.evaluateProduct(request);
    }

    // Generate lại toàn bộ
    public Call<String> generateAll() {
        return apiService.generateAll();
    }

    // Xóa evaluation
    public Call<Void> deleteEvaluation(int id) {
        return apiService.deleteEvaluation(id);
    }
}