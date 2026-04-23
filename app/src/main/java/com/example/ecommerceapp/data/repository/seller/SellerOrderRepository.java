package com.example.ecommerceapp.data.repository.seller;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;

import retrofit2.Call;

public class SellerOrderRepository {

    private final SellerOrderService service;

    public SellerOrderRepository(TokenManager tm) {
        service = ApiClient.getOrderService(tm);
    }

    public Call<PageResponse<SellerOrderResponse>> getOrders(
            String status, int page, int size
    ) {
        return service.getOrdersByStatus(status, page, size);
    }

    public Call<SellerOrderDetailResponse> getOrderDetail(int id) {
        return service.getOrderDetail(id);
    }

    public Call<Void> updateOrderStatus(int orderId, String status) {
        return service.updateOrderStatus(orderId, status);
    }
}