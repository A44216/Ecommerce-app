package com.example.ecommerceapp.ui.viewmodel.seller;

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

    private final SellerReviewRepository repository;

    private final Map<String, MutableLiveData<PageResponse<SellerReviewResponse>>> cache = new HashMap<>();
    private final MutableLiveData<Boolean> replyResult = new MutableLiveData<>();

    private String currentSort = "rating_desc,time_desc";

    public SellerReviewViewModel(TokenManager tm) {
        repository = new SellerReviewRepository(tm);
    }

    private String key(int productId, Boolean isReplied) {
        return productId + "_" + isReplied;
    }

    public LiveData<PageResponse<SellerReviewResponse>> getReviewsLiveData(int productId, Boolean isReplied) {
        String k = key(productId, isReplied);

        if (!cache.containsKey(k)) {
            cache.put(k, new MutableLiveData<>());
        }

        return cache.get(k);
    }

    public void loadReviews(int productId, Boolean isReplied, int page, int size) {

        if (page == 0) {
            String k = key(productId, isReplied);
            MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(k);

            if (liveData != null) {
                liveData.setValue(null); // reset UI
            }
        }

        repository.getReviews(productId, isReplied, page, size, currentSort)
                .enqueue(new Callback<PageResponse<SellerReviewResponse>>() {

                    @Override
                    public void onResponse(Call<PageResponse<SellerReviewResponse>> call,
                                           Response<PageResponse<SellerReviewResponse>> response) {

                        String k = key(productId, isReplied);
                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(k);

                        if (liveData == null) {
                            liveData = new MutableLiveData<>();
                            cache.put(k, liveData);
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            liveData.setValue(response.body());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerReviewResponse>> call, Throwable t) {

                        MutableLiveData<PageResponse<SellerReviewResponse>> liveData =
                                cache.get(key(productId, isReplied));

                        if (liveData != null) liveData.setValue(null);
                    }
                });
    }

    public void setSort(String sort, int productId, Boolean isReplied) {
        currentSort = sort;

        String k = key(productId, isReplied);

        MutableLiveData<PageResponse<SellerReviewResponse>> liveData = cache.get(k);

        if (liveData == null) {
            liveData = new MutableLiveData<>();
            cache.put(k, liveData);
        }

        liveData.setValue(null); // clear data
    }

    public void replyReview(int reviewId, int productId, Boolean isReplied, String content) {

        SellerReplyRequest request = new SellerReplyRequest(content);

        repository.replyReview(reviewId, request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            replyResult.setValue(true);

                            // 🔥 reload cả 2 tab
                            loadReviews(productId, false, 0, 10);
                            loadReviews(productId, true, 0, 10);

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