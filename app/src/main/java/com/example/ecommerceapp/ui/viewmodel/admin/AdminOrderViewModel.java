package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.repository.admin.AdminOrderRepository;
import com.example.ecommerceapp.data.repository.admin.AdminShopRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderViewModel extends ViewModel {

    private final AdminOrderRepository orderRepository;
    private final AdminShopRepository shopRepository;

    private final Map<String, MutableLiveData<List<AdminOrderResponse>>> cache = new HashMap<>();
    private final Map<String, Integer> currentPageMap = new HashMap<>();
    private final Map<String, Boolean> lastPageMap = new HashMap<>();

    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<String>> orderAutocompleteSuggestions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> shopAutocompleteSuggestions = new MutableLiveData<>();

    private final MutableLiveData<FilterState> currentFilter = new MutableLiveData<>(new FilterState());

    public static class FilterState {
        public String paymentMethod = null;
        public String paymentStatus = null;
        public String keyword = null;
        public Integer shopId = null;
        public String shopName = null;
        public String sortBy = "createdAt";
        public String direction = "desc";
    }

    public AdminOrderViewModel(AdminOrderRepository orderRepository, AdminShopRepository shopRepository) {
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<String>> getOrderAutocompleteSuggestions() {
        return orderAutocompleteSuggestions;
    }

    public LiveData<List<String>> getShopAutocompleteSuggestions() {
        return shopAutocompleteSuggestions;
    }

    public LiveData<FilterState> getFilterState() {
        return currentFilter;
    }

    public void updateFilter(String paymentMethod, String paymentStatus, String keyword, Integer shopId, String shopName, String sortBy, String direction) {
        FilterState filter = new FilterState();
        filter.paymentMethod = paymentMethod;
        filter.paymentStatus = paymentStatus;
        filter.keyword = keyword;
        filter.shopId = shopId;
        filter.shopName = shopName;
        filter.sortBy = sortBy;
        filter.direction = direction;
        currentFilter.setValue(filter);
    }

    public void autocompleteOrders(String keyword, Integer shopId) {
        orderRepository.autocompleteOrders(keyword, shopId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderAutocompleteSuggestions.setValue(response.body());
                } else {
                    orderAutocompleteSuggestions.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                orderAutocompleteSuggestions.setValue(new ArrayList<>());
            }
        });
    }

    public void autocompleteShops(String keyword) {
        shopRepository.autocomplete(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shopAutocompleteSuggestions.setValue(response.body());
                } else {
                    shopAutocompleteSuggestions.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                shopAutocompleteSuggestions.setValue(new ArrayList<>());
            }
        });
    }

    private String buildCacheKey(String status, String paymentMethod, String paymentStatus, String keyword, Integer shopId, String sortBy, String direction) {
        return status + "|" 
               + (paymentMethod != null ? paymentMethod : "") + "|" 
               + (paymentStatus != null ? paymentStatus : "") + "|" 
               + (keyword != null ? keyword : "") + "|" 
               + (shopId != null ? shopId : "") + "|" 
               + (sortBy != null ? sortBy : "createdAt") + "|" 
               + (direction != null ? direction : "desc");
    }

    public LiveData<List<AdminOrderResponse>> getOrders(String status, String paymentMethod, String paymentStatus, String keyword, Integer shopId, String sortBy, String direction) {
        String key = buildCacheKey(status, paymentMethod, paymentStatus, keyword, shopId, sortBy, direction);
        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            loadOrders(status, paymentMethod, paymentStatus, keyword, shopId, sortBy, direction, false);
        }
        return cache.get(key);
    }

    public void loadOrders(String status, String paymentMethod, String paymentStatus, String keyword, Integer shopId, String sortBy, String direction, boolean isLoadMore) {
        String key = buildCacheKey(status, paymentMethod, paymentStatus, keyword, shopId, sortBy, direction);
        int page = currentPageMap.getOrDefault(key, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(key, 0);
            lastPageMap.put(key, false);
        }

        if (Boolean.TRUE.equals(lastPageMap.get(key))) return;

        orderRepository.getOrders(
                page,
                PAGE_SIZE,
                keyword,
                shopId,
                status,
                paymentMethod,
                paymentStatus,
                sortBy,
                direction
        ).enqueue(new Callback<PageResponse<AdminOrderResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<AdminOrderResponse>> call, Response<PageResponse<AdminOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<AdminOrderResponse> body = response.body();
                    MutableLiveData<List<AdminOrderResponse>> liveData = cache.get(key);
                    if (liveData == null) {
                        liveData = new MutableLiveData<>();
                        cache.put(key, liveData);
                    }

                    List<AdminOrderResponse> current = liveData.getValue();
                    if (current == null) current = new ArrayList<>();

                    List<AdminOrderResponse> newItems = body.getItems();
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
                } else {
                    error.setValue("Lỗi tải danh sách đơn hàng: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<AdminOrderResponse>> call, Throwable t) {
                error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void clearCache(String status) {
        for (String key : cache.keySet()) {
            if (key.startsWith(status + "|")) {
                cache.get(key).setValue(null);
            }
        }
    }
}
