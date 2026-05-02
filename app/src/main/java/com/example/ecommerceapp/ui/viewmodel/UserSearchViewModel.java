package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.UserPageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.repository.UserProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchViewModel extends ViewModel {

    private final MutableLiveData<List<UserProductResponse>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<String>> suggestions = new MutableLiveData<>();
    private final MutableLiveData<List<UserProductResponse>> trendingProducts = new MutableLiveData<>();

    // Pagination state
    private int currentPage = 0;
    private boolean isLastPage = false;
    private String currentKeyword = "";
    private String currentSortBy = "id,desc"; // Mặc định: Mới nhất
    private final int PAGE_SIZE = 10;

    private final UserProductRepository productRepository;

    public UserSearchViewModel(UserProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public LiveData<List<UserProductResponse>> getSearchResults() { return searchResults; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<String>> getSuggestions() { return suggestions; }
    public LiveData<List<UserProductResponse>> getTrendingProducts() { return trendingProducts; }

    /**
     * Tìm kiếm sản phẩm (Hỗ trợ phân trang)
     */
    public void searchProducts(String keyword, boolean isRefresh) {
        if (isRefresh) {
            currentPage = 0;
            isLastPage = false;
            currentKeyword = keyword;
        }

        if (Boolean.TRUE.equals(isLoading.getValue()) || isLastPage) return;

        isLoading.setValue(true);
        productRepository.searchProductsPaginated(currentKeyword, currentPage, PAGE_SIZE, currentSortBy).enqueue(new Callback<UserPageResponse<UserProductResponse>>() {
            @Override
            public void onResponse(Call<UserPageResponse<UserProductResponse>> call, Response<UserPageResponse<UserProductResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    UserPageResponse<UserProductResponse> pageData = response.body();
                    isLastPage = pageData.isLast();

                    List<UserProductResponse> currentList = searchResults.getValue();
                    if (isRefresh || currentList == null) {
                        searchResults.setValue(new ArrayList<>(pageData.getContent()));
                    } else {
                        List<UserProductResponse> updatedList = new ArrayList<>(currentList);
                        updatedList.addAll(pageData.getContent());
                        searchResults.setValue(updatedList);
                    }
                    currentPage++;
                } else {
                    errorMessage.setValue("Không thể lấy kết quả tìm kiếm");
                }
            }

            @Override
            public void onFailure(Call<UserPageResponse<UserProductResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void fetchSuggestions(String keyword) {
        productRepository.suggestProducts(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {}
        });
    }

    public void fetchTrendingProducts() {
        isLoading.setValue(true);
        productRepository.getTrendingProducts().enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    trendingProducts.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }

    public void setSortBy(String sortBy) {
        if (sortBy != null && !sortBy.equals(currentSortBy)) {
            this.currentSortBy = sortBy;
            if (!currentKeyword.isEmpty()) {
                searchProducts(currentKeyword, true);
            }
        }
    }

    public String getCurrentSortBy() {
        return currentSortBy;
    }
}
