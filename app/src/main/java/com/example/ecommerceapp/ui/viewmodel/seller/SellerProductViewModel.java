package com.example.ecommerceapp.ui.viewmodel.seller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.SellerProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductViewModel extends ViewModel {

    private final SellerProductRepository repository;

    private String keyword = "";
    private String status = "";
    private Boolean isDeleted = false;

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    private final Map<String, MutableLiveData<List<SellerProductResponse>>> cache = new HashMap<>();
    private final Map<String, Integer> currentPageMap = new HashMap<>();
    private final Map<String, Boolean> lastPageMap = new HashMap<>();

    private static final int PAGE_SIZE = 10;

    public SellerProductViewModel(SellerProductRepository repository) {
        this.repository = repository;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    private String getKey() {
        return status + "_" + isDeleted;
    }

    public LiveData<List<SellerProductResponse>> getProducts() {
        String key = getKey();

        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            fetchProducts(false);
        }

        return cache.get(key);
    }

    public void fetchProducts(boolean isLoadMore) {
        String key = getKey();

        int page = currentPageMap.getOrDefault(key, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(key, 0);
            lastPageMap.put(key, false);
        }

        if (Boolean.TRUE.equals(lastPageMap.get(key))) return;

        repository.getProducts(status, isDeleted, keyword, page, PAGE_SIZE)
                .enqueue(new Callback<PageResponse<SellerProductResponse>>() {

                    @Override
                    public void onResponse(Call<PageResponse<SellerProductResponse>> call,
                                           Response<PageResponse<SellerProductResponse>> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        PageResponse<SellerProductResponse> body = response.body();

                        MutableLiveData<List<SellerProductResponse>> liveData = cache.get(key);
                        if (liveData == null) {
                            liveData = new MutableLiveData<>();
                            cache.put(key, liveData);
                        }

                        List<SellerProductResponse> current = liveData.getValue();
                        if (current == null) current = new java.util.ArrayList<>();

                        List<SellerProductResponse> newItems = body.getItems();
                        if (newItems == null) newItems = new java.util.ArrayList<>();

                        if (isLoadMore) {
                            current.addAll(newItems);
                        } else {
                            current = new java.util.ArrayList<>(newItems);
                        }

                        liveData.setValue(current);

                        currentPageMap.put(key, body.getPage() + 1);

                        boolean isLast = newItems.size() < PAGE_SIZE
                                || current.size() >= body.getTotalElements();

                        lastPageMap.put(key, isLast);
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerProductResponse>> call, Throwable t) {
                        Log.e("API", "FAIL: " + t.getMessage());
                    }
                });
    }

    public LiveData<SellerProductResponse> getProductById(int id) {

        MutableLiveData<SellerProductResponse> data = new MutableLiveData<>();

        repository.getProductById(id)
                .enqueue(new Callback<SellerProductResponse>() {
                    @Override
                    public void onResponse(Call<SellerProductResponse> call,
                                           Response<SellerProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<SellerProductResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }

    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();

    public void deleteProduct(int id) {
        // OPTIMISTIC UPDATE: xóa khỏi cache ngay lập tức
        String key = getKey();
        MutableLiveData<List<SellerProductResponse>> liveData = cache.get(key);
        if (liveData != null) {
            List<SellerProductResponse> current = liveData.getValue();
            if (current != null) {
                List<SellerProductResponse> updated = new java.util.ArrayList<>(current);
                updated.removeIf(p -> java.util.Objects.equals(p.getId(), id));
                liveData.setValue(updated);
            }
        }

        // Gọi API xóa ở background
        repository.deleteProduct(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            deleteResult.setValue(false);
                            // Nếu API thất bại, fetch lại để đồng bộ
                            fetchProducts(false);
                        } else {
                            deleteResult.setValue(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        deleteResult.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    private final MutableLiveData<Boolean> restoreResult = new MutableLiveData<>();

    public LiveData<Boolean> getRestoreResult() {
        return restoreResult;
    }

    public void restoreProduct(int id) {

        // OPTIMISTIC UPDATE: xóa khỏi cache ngay lập tức
        String key = getKey();
        MutableLiveData<List<SellerProductResponse>> liveData = cache.get(key);
        if (liveData != null) {
            List<SellerProductResponse> current = liveData.getValue();
            if (current != null) {
                List<SellerProductResponse> updated = new java.util.ArrayList<>(current);
                updated.removeIf(p -> java.util.Objects.equals(p.getId(), id));
                liveData.setValue(updated);
            }
        }

        repository.restoreProduct(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            restoreResult.setValue(true);
                        } else {
                            restoreResult.setValue(false);
                            fetchProducts(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        restoreResult.setValue(false);
                    }
                });
    }

    private final MutableLiveData<Boolean> submitResult = new MutableLiveData<>();

    public LiveData<Boolean> getSubmitResult() {
        return submitResult;
    }

    public void submitProduct(int id) {

        // OPTIMISTIC UPDATE: xóa khỏi cache ngay lập tức
        String key = getKey();
        MutableLiveData<List<SellerProductResponse>> liveData = cache.get(key);
        if (liveData != null) {
            List<SellerProductResponse> current = liveData.getValue();
            if (current != null) {
                List<SellerProductResponse> updated = new java.util.ArrayList<>(current);
                updated.removeIf(p -> java.util.Objects.equals(p.getId(), id));
                liveData.setValue(updated);
            }
        }

        repository.submitProduct(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            submitResult.setValue(true);
                        } else {
                            submitResult.setValue(false);
                            fetchProducts(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        submitResult.setValue(false);
                    }
                });
    }

    private final MutableLiveData<List<String>> autocompleteResult = new MutableLiveData<>();

    public void autocompleteProducts(String keyword) {
        repository.autocompleteProducts(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    autocompleteResult.setValue(response.body());
                } else {
                    autocompleteResult.setValue(new java.util.ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                autocompleteResult.setValue(new java.util.ArrayList<>());
            }
        });
    }

    public LiveData<List<String>> getAutocompleteResult() {
        return autocompleteResult;
    }
}