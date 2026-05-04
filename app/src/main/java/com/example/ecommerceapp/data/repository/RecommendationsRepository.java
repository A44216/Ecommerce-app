package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.RecommendationService;
import com.example.ecommerceapp.data.model.response.seller.RecommendationResponse;

import java.util.List;

import retrofit2.Call;

public class RecommendationsRepository {

    private final RecommendationService apiService;

    public RecommendationsRepository(RecommendationService apiService) {
        this.apiService = apiService;
    }

    // Lấy danh sách recommendation
    public Call<List<RecommendationResponse>> getMyRecommendations(int limit) {
        return apiService.getMyRecommendations(limit);
    }

    // Refresh lại recommendation
    public Call<Void> refreshRecommendations() {
        return apiService.refreshRecommendations();
    }
}