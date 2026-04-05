package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.local.TokenManager;
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

    // cache theo status
    private final Map<String, MutableLiveData<List<SellerOrderResponse>>> cache = new HashMap<>();

    public SellerOrderViewModel(TokenManager tm) {
        repository = new OrderRepository(tm);
    }

    public LiveData<List<SellerOrderResponse>> getOrders(String status) {

        if (!cache.containsKey(status)) {
            cache.put(status, new MutableLiveData<>());
            loadOrders(status);
        }

        return cache.get(status);
    }

    public void loadOrders(String status) {

        repository.getOrders(status).enqueue(new Callback<List<SellerOrderResponse>>() {
            @Override
            public void onResponse(Call<List<SellerOrderResponse>> call, Response<List<SellerOrderResponse>> response) {

                if (response.isSuccessful()) {
                    cache.get(status).setValue(response.body());
                } else {
                    cache.get(status).setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SellerOrderResponse>> call, Throwable t) {
                cache.get(status).setValue(null);
            }
        });
    }
}