package com.example.ecommerceapp.data.repository.seller.order;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerReviewService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.order.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.order.SellerReviewResponse;

import java.util.List;

import retrofit2.Call;

public class SellerReviewRepository {

    private final SellerReviewService service;

    public SellerReviewRepository(TokenManager tm) {
        service = ApiClient.getReviewService(tm);
    }

    public Call<List<SellerReviewResponse>> getReviews(Integer productId) {
        return service.getReviews(productId);
    }

    public Call<Void> replyReview(int reviewId, SellerReplyRequest request) {
        return service.replyReview(reviewId, request);
    }
}