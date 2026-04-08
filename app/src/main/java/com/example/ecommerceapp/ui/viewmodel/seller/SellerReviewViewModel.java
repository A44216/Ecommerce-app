package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.PageResponse;
import com.example.ecommerceapp.data.model.request.seller.order.SellerReplyRequest;
import com.example.ecommerceapp.data.model.response.seller.order.SellerReviewResponse;
import com.example.ecommerceapp.data.repository.seller.order.SellerReviewRepository;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerReviewViewModel extends ViewModel {

    private final SellerReviewRepository repository;

    private final Map<Integer, MutableLiveData<PageResponse<SellerReviewResponse>>> cache = new HashMap<>();

    private final MutableLiveData<Boolean> replyResult = new MutableLiveData<>();

    public SellerReviewViewModel(TokenManager tm) {
        repository = new SellerReviewRepository(tm);
    }

    // ===== GET REVIEWS =====
    public LiveData<PageResponse<SellerReviewResponse>> getReviews(
            Integer productId,
            Boolean isReplied,
            int page,
            int size
    ) {

        if (!cache.containsKey(productId)) {
            cache.put(productId, new MutableLiveData<>());
        }

        loadReviews(productId, isReplied, page, size);

        return cache.get(productId);
    }

    public void loadReviews(
            Integer productId,
            Boolean isReplied,
            int page,
            int size
    ) {

        repository.getReviews(productId, isReplied, page, size)
                .enqueue(new Callback<PageResponse<SellerReviewResponse>>() {

                    @Override
                    public void onResponse(
                            Call<PageResponse<SellerReviewResponse>> call,
                            Response<PageResponse<SellerReviewResponse>> response
                    ) {

                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(productId);

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
                    public void onFailure(Call<PageResponse<SellerReviewResponse>> call, Throwable t) {

                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(productId);

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

                            // reload page 0
                            loadReviews(productId, null, 0, 10);

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