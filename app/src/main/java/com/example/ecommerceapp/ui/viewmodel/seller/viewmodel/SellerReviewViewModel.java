package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.order.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.order.SellerReviewResponse;
import com.example.ecommerceapp.data.repository.seller.order.SellerReviewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerReviewViewModel extends ViewModel {

    private final SellerReviewRepository repository;

    // cache theo productId
    private final Map<Integer, MutableLiveData<List<SellerReviewResponse>>> cache = new HashMap<>();

    private final MutableLiveData<Boolean> replyResult = new MutableLiveData<>();

    public SellerReviewViewModel(TokenManager tm) {
        repository = new SellerReviewRepository(tm);
    }

    // ===== GET REVIEWS =====
    public LiveData<List<SellerReviewResponse>> getReviews(Integer productId) {

        if (!cache.containsKey(productId)) {
            cache.put(productId, new MutableLiveData<>());
            loadReviews(productId);
        }

        return cache.get(productId);
    }

    public void loadReviews(Integer productId) {

        repository.getReviews(productId).enqueue(new Callback<List<SellerReviewResponse>>() {
            @Override
            public void onResponse(Call<List<SellerReviewResponse>> call,
                                   Response<List<SellerReviewResponse>> response) {

                MutableLiveData<List<SellerReviewResponse>> liveData = cache.get(productId);

                if (liveData == null) {
                    liveData = new MutableLiveData<>();
                    cache.put(productId, liveData);
                }

                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SellerReviewResponse>> call, Throwable t) {

                MutableLiveData<List<SellerReviewResponse>> liveData = cache.get(productId);

                if (liveData != null) {
                    liveData.setValue(null);
                }
            }
        });
    }

    // ===== REPLY REVIEW =====
    public void replyReview(int reviewId, int productId, String content) {

        SellerReplyRequest request = new SellerReplyRequest(content);

        repository.replyReview(reviewId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            replyResult.setValue(true);

                            // reload lại đúng product
                            loadReviews(productId);

                        } else {
                            replyResult.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        replyResult.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> getReplyResult() {
        return replyResult;
    }
}