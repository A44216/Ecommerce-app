package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.seller.RecommendationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RecommendationService {

    // Lấy danh sách gợi ý
    @GET("recommendations/my-recs")
    Call<List<RecommendationResponse>> getMyRecommendations(
            @Query("limit") int limit
    );

    // Refresh lại gợi ý (không trả dữ liệu)
    @POST("recommendations/refresh")
    Call<Void> refreshRecommendations();
}