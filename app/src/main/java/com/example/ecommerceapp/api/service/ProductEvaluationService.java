package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ProductEvaluationRequest;
import com.example.ecommerceapp.data.model.response.ProductEvaluationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ProductEvaluationService {

    // Top deal (Fuzzy)
    @GET("product-evaluations/top-deals")
    Call<List<ProductEvaluationResponse>> getTopDeals(
            @Query("limit") int limit
    );

    // Trending
    @GET("product-evaluations/trending")
    Call<List<ProductEvaluationResponse>> getTrendingDeals(
            @Query("limit") int limit
    );

    // Evaluate 1 product
    @POST("product-evaluations/evaluate")
    Call<ProductEvaluationResponse> evaluateProduct(
            @Body ProductEvaluationRequest request
    );

    // Generate all (trả về String message)
    @POST("product-evaluations/generate-all")
    Call<String> generateAll();

    // Delete evaluation
    @DELETE("product-evaluations/{id}")
    Call<Void> deleteEvaluation(
            @Path("id") int id
    );
}