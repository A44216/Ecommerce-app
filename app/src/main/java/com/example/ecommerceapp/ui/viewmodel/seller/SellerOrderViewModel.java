package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;
import com.example.ecommerceapp.data.repository.seller.SellerOrderRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerOrderViewModel extends ViewModel {

    private final SellerOrderRepository repository;

    private final Map<String, MutableLiveData<List<SellerOrderResponse>>> cache = new HashMap<>();
    private final Map<String, Integer> currentPageMap = new HashMap<>();
    private final Map<String, Boolean> lastPageMap = new HashMap<>();

    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<Boolean> updateStatusResult = new MutableLiveData<>();
    private final MutableLiveData<List<String>> autocompleteResult = new MutableLiveData<>();

    // Filter state - shared giữa parent và children
    private final MutableLiveData<FilterState> currentFilter = new MutableLiveData<>(new FilterState());

    public static class FilterState {
        public String paymentMethod = null;
        public String paymentStatus = null;
        public String keyword = null;
    }

    public SellerOrderViewModel(SellerOrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<SellerOrderResponse>> getOrders(String status) {
        String key = status;
        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            loadOrders(status, null, null, null, false);
        }
        return cache.get(key);
    }

    public LiveData<List<SellerOrderResponse>> getOrders(String status, String paymentMethod, String paymentStatus, String keyword) {
        String key = buildCacheKey(status, paymentMethod, paymentStatus, keyword);
        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            loadOrders(status, paymentMethod, paymentStatus, keyword, false);
        }
        return cache.get(key);
    }

    private String buildCacheKey(String status, String paymentMethod, String paymentStatus, String keyword) {
        return status + "|" + (paymentMethod != null ? paymentMethod : "") + "|" 
               + (paymentStatus != null ? paymentStatus : "") + "|" + (keyword != null ? keyword : "");
    }

    public void loadOrders(String status, boolean isLoadMore) {
        loadOrders(status, null, null, null, isLoadMore);
    }

    public void loadOrders(String status, String paymentMethod, String paymentStatus, String keyword, boolean isLoadMore) {
        String key = buildCacheKey(status, paymentMethod, paymentStatus, keyword);
        int page = currentPageMap.getOrDefault(key, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(key, 0);
            lastPageMap.put(key, false);
        }

        if (Boolean.TRUE.equals(lastPageMap.get(key))) return;

        repository.getOrders(status, paymentMethod, paymentStatus, keyword, page, PAGE_SIZE)
                .enqueue(new Callback<PageResponse<SellerOrderResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<SellerOrderResponse>> call,
                                           Response<PageResponse<SellerOrderResponse>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        PageResponse<SellerOrderResponse> body = response.body();
                        MutableLiveData<List<SellerOrderResponse>> liveData = cache.get(key);
                        if (liveData == null) {
                            liveData = new MutableLiveData<>();
                            cache.put(key, liveData);
                        }

                        List<SellerOrderResponse> current = liveData.getValue();
                        if (current == null) current = new ArrayList<>();

                        List<SellerOrderResponse> newItems = body.getItems();
                        if (newItems == null) newItems = new ArrayList<>();

                        if (isLoadMore) {
                            current.addAll(newItems);
                        } else {
                            current = new ArrayList<>(newItems);
                        }

                        liveData.setValue(current);
                        currentPageMap.put(key, body.getPage() + 1);

                        boolean isLast = newItems.size() < PAGE_SIZE || current.size() >= body.getTotalElements();
                        lastPageMap.put(key, isLast);
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerOrderResponse>> call, Throwable t) {}
                });
    }

    public LiveData<SellerOrderDetailResponse> getOrderDetail(int orderId) {
        MutableLiveData<SellerOrderDetailResponse> data = new MutableLiveData<>();
        repository.getOrderDetail(orderId).enqueue(new Callback<SellerOrderDetailResponse>() {
            @Override
            public void onResponse(Call<SellerOrderDetailResponse> call, Response<SellerOrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SellerOrderDetailResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        repository.updateOrderStatus(orderId, status.name()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateStatusResult.setValue(true);
                    for (String key : cache.keySet()) {
                        cache.get(key).setValue(null);
                    }
                } else {
                    updateStatusResult.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                updateStatusResult.setValue(false);
            }
        });
    }

    public LiveData<Boolean> getUpdateStatusResult() {
        return updateStatusResult;
    }

    public void autocompleteOrders(String keyword) {
        repository.autocompleteOrders(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    autocompleteResult.setValue(response.body());
                } else {
                    autocompleteResult.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                autocompleteResult.setValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<String>> getAutocompleteResult() {
        return autocompleteResult;
    }

    public void clearCache(String status) {
        for (String key : cache.keySet()) {
            if (key.startsWith(status + "|")) {
                cache.get(key).setValue(null);
            }
        }
    }

    // Filter methods - để parent gọi
    public void updateFilter(String paymentMethod, String paymentStatus, String keyword) {
        FilterState filter = new FilterState();
        filter.paymentMethod = paymentMethod;
        filter.paymentStatus = paymentStatus;
        filter.keyword = keyword;
        currentFilter.setValue(filter);
    }

    public LiveData<FilterState> getFilter() {
        return currentFilter;
    }
}