package com.example.ecommerceapp.data.repository.seller.review;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerReviewService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.PageResponse;
import com.example.ecommerceapp.data.model.request.seller.review.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.review.SellerReviewResponse;

import retrofit2.Call;

public class SellerReviewRepository {

    private final SellerReviewService service;

    public SellerReviewRepository(TokenManager tm) {
        service = ApiClient.getReviewService(tm);
    }

    // FIX: Page thay vì List
    public Call<PageResponse<SellerReviewResponse>> getReviews(
            Integer productId,
            Boolean isReplied,
            int page,
            int size
    ) {
        return service.getReviews(productId, isReplied, page, size);
    }

    public Call<Void> replyReview(int reviewId, SellerReplyRequest request) {
        return service.replyReview(reviewId, request);
    }
}