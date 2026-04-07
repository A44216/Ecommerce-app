package com.example.ecommerceapp.data.repository.seller.order;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;

import java.util.List;

import retrofit2.Call;

public class SellerOrderRepository {

    private final SellerOrderService service;

    public SellerOrderRepository(TokenManager tm) {
        service = ApiClient.getOrderService(tm);
    }

    public Call<List<SellerOrderResponse>> getOrders(String status, int shopId) {
        return service.getOrdersByStatus(shopId, status);
    }

    public Call<SellerOrderDetailResponse> getOrderDetail(int id, int shopId) {
        return service.getOrderDetail(id, shopId);
    }

    public Call<Void> updateOrderStatus(int orderId, int shopId, String status) {
        return service.updateOrderStatus(orderId, shopId, status);
    }
}