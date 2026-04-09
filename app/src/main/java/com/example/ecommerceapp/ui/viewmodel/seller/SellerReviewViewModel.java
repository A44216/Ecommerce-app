package com.example.ecommerceapp.ui.viewmodel.seller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.review.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.review.SellerReviewResponse;
import com.example.ecommerceapp.data.repository.seller.review.SellerReviewRepository;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerReviewViewModel extends ViewModel {

    private static final String TAG = "SellerReviewVM";

    private final SellerReviewRepository repository;

    private final Map<Integer, MutableLiveData<PageResponse<SellerReviewResponse>>> cache = new HashMap<>();

    private final MutableLiveData<Boolean> replyResult = new MutableLiveData<>();

    public SellerReviewViewModel(TokenManager tm) {
        repository = new SellerReviewRepository(tm);
    }

    // LIVE DATA ONLY (NO API CALL)
    public LiveData<PageResponse<SellerReviewResponse>> getReviewsLiveData(int productId) {

        if (!cache.containsKey(productId)) {
            cache.put(productId, new MutableLiveData<>());
        }

        return cache.get(productId);
    }

    // LOAD API (ONLY PLACE CALL API)
    public void loadReviews(int productId, Boolean isReplied, int page, int size) {

        Log.d(TAG, "loadReviews productId=" + productId + " page=" + page);

        repository.getReviews(productId, isReplied, page, size)
                .enqueue(new Callback<PageResponse<SellerReviewResponse>>() {

                    @Override
                    public void onResponse(Call<PageResponse<SellerReviewResponse>> call,
                                           Response<PageResponse<SellerReviewResponse>> response) {

                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(productId);

                        if (liveData == null) {
                            liveData = new MutableLiveData<>();
                            cache.put(productId, liveData);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "API success page=" + page);
                            liveData.setValue(response.body());
                        } else {
                            Log.e(TAG, "API failed");
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerReviewResponse>> call, Throwable t) {
                        Log.e(TAG, "API error: " + t.getMessage());

                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(productId);
                        if (liveData != null) {
                            liveData.setValue(null);
                        }
                    }
                });
    }

    // REPLY REVIEW
    public void replyReview(int reviewId, int productId, String content) {

        Log.d(TAG, "replyReview id=" + reviewId);

        SellerReplyRequest request = new SellerReplyRequest(content);

        repository.replyReview(reviewId, request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            replyResult.setValue(true);

                            Log.d(TAG, "Reply success → reload page 0");

                            loadReviews(productId, null, 0, 10);

                        } else {
                            replyResult.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        replyResult.setValue(false);
                        Log.e(TAG, "Reply error: " + t.getMessage());
                    }
                });
    }

    public LiveData<Boolean> getReplyResult() {
        return replyResult;
    }
}