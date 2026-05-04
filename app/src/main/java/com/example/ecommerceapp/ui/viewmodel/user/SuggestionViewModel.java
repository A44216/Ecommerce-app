package com.example.ecommerceapp.ui.viewmodel.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.ProductEvaluationResponse;
import com.example.ecommerceapp.data.model.response.seller.RecommendationResponse;
import com.example.ecommerceapp.data.model.ui.SuggestionDisplayItem;
import com.example.ecommerceapp.data.repository.ProductEvaluationRepository;
import com.example.ecommerceapp.data.repository.RecommendationsRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionViewModel extends ViewModel {

    public enum SuggestionType {
        PERSONALIZED, TRENDING, FUZZY
    }

    private final RecommendationsRepository recRepo;
    private final ProductEvaluationRepository evalRepo;

    private final MutableLiveData<List<SuggestionDisplayItem>> suggestionList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SuggestionViewModel(RecommendationsRepository recRepo, ProductEvaluationRepository evalRepo) {
        this.recRepo = recRepo;
        this.evalRepo = evalRepo;
    }

    public LiveData<List<SuggestionDisplayItem>> getSuggestionList() { return suggestionList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchSuggestions(SuggestionType type) {
        isLoading.setValue(true);
        switch (type) {
            case PERSONALIZED:
                fetchPersonalized();
                break;
            case TRENDING:
                fetchTrending();
                break;
            case FUZZY:
                fetchFuzzy();
                break;
        }
    }

    private void fetchPersonalized() {
        recRepo.getMyRecommendations(20).enqueue(new Callback<List<RecommendationResponse>>() {
            @Override
            public void onResponse(Call<List<RecommendationResponse>> call, Response<List<RecommendationResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SuggestionDisplayItem> items = new ArrayList<>();
                    for (RecommendationResponse rec : response.body()) {
                        items.add(new SuggestionDisplayItem(rec));
                    }
                    suggestionList.setValue(items);
                } else {
                    errorMessage.setValue("Không thể lấy gợi ý cá nhân");
                }
            }

            @Override
            public void onFailure(Call<List<RecommendationResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void fetchTrending() {
        evalRepo.getTrendingDeals(20).enqueue(new Callback<List<ProductEvaluationResponse>>() {
            @Override
            public void onResponse(Call<List<ProductEvaluationResponse>> call, Response<List<ProductEvaluationResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SuggestionDisplayItem> items = new ArrayList<>();
                    for (ProductEvaluationResponse eval : response.body()) {
                        items.add(new SuggestionDisplayItem(eval));
                    }
                    suggestionList.setValue(items);
                } else {
                    errorMessage.setValue("Không thể lấy sản phẩm xu hướng");
                }
            }

            @Override
            public void onFailure(Call<List<ProductEvaluationResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void fetchFuzzy() {
        evalRepo.getTopDeals(20).enqueue(new Callback<List<ProductEvaluationResponse>>() {
            @Override
            public void onResponse(Call<List<ProductEvaluationResponse>> call, Response<List<ProductEvaluationResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SuggestionDisplayItem> items = new ArrayList<>();
                    for (ProductEvaluationResponse eval : response.body()) {
                        items.add(new SuggestionDisplayItem(eval));
                    }
                    suggestionList.setValue(items);
                } else {
                    errorMessage.setValue("Không thể lấy deal hời");
                }
            }

            @Override
            public void onFailure(Call<List<ProductEvaluationResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
