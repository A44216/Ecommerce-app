package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;
import com.example.ecommerceapp.data.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerOrderViewModel extends ViewModel {

    private final OrderRepository repository;

    private final Map<String, MutableLiveData<List<SellerOrderResponse>>> cache = new HashMap<>();
    private final MutableLiveData<Boolean> updateStatusResult = new MutableLiveData<>();

    public SellerOrderViewModel(TokenManager tm) {
        repository = new OrderRepository(tm);
    }

    public LiveData<List<SellerOrderResponse>> getOrders(String status, int shopId) {

        String key = status + "_" + shopId;

        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
            loadOrders(status, shopId);
        }

        return cache.get(key);
    }

    public void loadOrders(String status, int shopId) {

        String key = status + "_" + shopId;

        repository.getOrders(status, shopId).enqueue(new Callback<List<SellerOrderResponse>>() {
            @Override
            public void onResponse(Call<List<SellerOrderResponse>> call,
                                   Response<List<SellerOrderResponse>> response) {

                if (response.isSuccessful()) {
                    cache.get(key).setValue(response.body());
                } else {
                    cache.get(key).setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SellerOrderResponse>> call, Throwable t) {
                cache.get(key).setValue(null);
            }
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

    public LiveData<Boolean> getUpdateStatusResult() {
        return updateStatusResult;
    }

    public void updateOrderStatus(int orderId, int shopId, OrderStatus status) {

        repository.updateOrderStatus(orderId, shopId, status.name())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        updateStatusResult.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        updateStatusResult.setValue(false);
                    }
                });
    }
}