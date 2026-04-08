package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.request.seller.PageResponse;
import com.example.ecommerceapp.data.model.request.seller.order.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.order.SellerReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface SellerReviewService {

    // GET /api/seller/reviews?productId=
    @GET("seller/reviews")
    Call<PageResponse<SellerReviewResponse>> getReviews(
            @Query("productId") Integer productId,
            @Query("isReplied") Boolean isReplied,
            @Query("page") int page,
            @Query("size") int size
    );

    // POST /api/seller/reviews/{reviewId}/reply
    @POST("seller/reviews/{reviewId}/reply")
    Call<Void> replyReview(
            @Path("reviewId") int reviewId,
            @Body SellerReplyRequest request
    );
}