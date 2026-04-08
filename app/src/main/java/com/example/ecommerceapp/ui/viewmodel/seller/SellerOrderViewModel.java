package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;
import com.example.ecommerceapp.data.repository.seller.order.SellerOrderRepository;

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

    public SellerOrderViewModel(TokenManager tm) {
        repository = new SellerOrderRepository(tm);
    }

    public LiveData<List<SellerOrderResponse>> getOrders(String status, int shopId) {

        String key = status + "_" + shopId;

        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            loadOrders(status, shopId, false);
        }

        return cache.get(key);
    }

    public void loadOrders(String status, int shopId, boolean isLoadMore) {

        String key = status + "_" + shopId;

        int page = currentPageMap.getOrDefault(key, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(key, 0);
            lastPageMap.put(key, false);
        }

        if (Boolean.TRUE.equals(lastPageMap.get(key))) return;

        repository.getOrders(status, shopId, page, PAGE_SIZE)
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
                        if (current == null) current = new java.util.ArrayList<>();

                        if (isLoadMore) {
                            current.addAll(body.getContent());
                        } else {
                            current = new java.util.ArrayList<>(body.getContent());
                        }

                        liveData.setValue(current);

                        currentPageMap.put(key, body.getNumber() + 1);
                        lastPageMap.put(key, body.isLast());
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerOrderResponse>> call, Throwable t) {}
                });
    }

    public LiveData<SellerOrderDetailResponse> getOrderDetail(int orderId, int shopId) {

        MutableLiveData<SellerOrderDetailResponse> data = new MutableLiveData<>();

        repository.getOrderDetail(orderId, shopId)
                .enqueue(new Callback<SellerOrderDetailResponse>() {
                    @Override
                    public void onResponse(Call<SellerOrderDetailResponse> call,
                                           Response<SellerOrderDetailResponse> response) {

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

    public void updateOrderStatus(int orderId, int shopId, OrderStatus status) {

        repository.updateOrderStatus(orderId, shopId, status.name())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {

                            updateStatusResult.setValue(true);

                            // Chỉ reset list để reload lại tất cả tab
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

}