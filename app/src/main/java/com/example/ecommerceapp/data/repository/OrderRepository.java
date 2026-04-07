package com.example.ecommerceapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final SellerOrderService service;

    public OrderRepository(TokenManager tm) {
        service = ApiClient.getOrderService(tm);
    }

    // ================= ORDER LIST =================
    public LiveData<List<SellerOrderResponse>> getOrders(String status, int shopId) {

        MutableLiveData<List<SellerOrderResponse>> data = new MutableLiveData<>();

        service.getOrdersByStatus(shopId, status)
                .enqueue(new Callback<List<SellerOrderResponse>>() {
                    @Override
                    public void onResponse(Call<List<SellerOrderResponse>> call,
                                           Response<List<SellerOrderResponse>> response) {

                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SellerOrderResponse>> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }

    // ================= ORDER DETAIL =================
    public LiveData<SellerOrderDetailResponse> getOrderDetail(int orderId, int shopId) {

        MutableLiveData<SellerOrderDetailResponse> data = new MutableLiveData<>();

        service.getOrderDetail(orderId, shopId)
                .enqueue(new Callback<SellerOrderDetailResponse>() {
                    @Override
                    public void onResponse(Call<SellerOrderDetailResponse> call,
                                           Response<SellerOrderDetailResponse> response) {

                        if (response.isSuccessful()) {
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

    // ================= UPDATE STATUS =================
    public LiveData<Boolean> updateOrderStatus(int orderId, int shopId, String status) {

        MutableLiveData<Boolean> result = new MutableLiveData<>();

        service.updateOrderStatus(orderId, shopId, status)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        result.setValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        result.setValue(false);
                    }
                });

        return result;
    }
}