package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;

import java.util.List;

import retrofit2.Call;

public class OrderRepository {

    private final SellerOrderService service;

    public OrderRepository(TokenManager tm) {
        service = ApiClient.getOrderService(tm);
    }

    public Call<List<SellerOrderResponse>> getOrders(String status, int shopId) {
        return service.getOrdersByStatus(shopId, status);
    }

    public Call<SellerOrderDetailResponse> getOrderDetail(int id, int shopId) {
        return service.getOrderDetail(id, shopId);
    }

}
