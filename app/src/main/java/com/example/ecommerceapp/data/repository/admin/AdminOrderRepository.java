package com.example.ecommerceapp.data.repository.admin;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminOrderService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import java.util.List;

import retrofit2.Call;

public class AdminOrderRepository {

    private final AdminOrderService service;

    public AdminOrderRepository(TokenManager tokenManager) {
        this.service = ApiClient.getAdminOrderService(tokenManager);
    }

    // GET ORDERS
    public Call<PageResponse<AdminOrderResponse>> getOrders(
            int page,
            int size,
            String keyword,
            Integer shopId,
            String status,
            String paymentMethod,
            String paymentStatus,
            String sortBy,
            String direction
    ) {
        return service.getOrders(
                page,
                size,
                keyword,
                shopId,
                status,
                paymentMethod,
                paymentStatus,
                sortBy,
                direction
        );
    }

    // GET ORDER DETAIL
    public Call<AdminOrderDetailResponse> getOrderById(int id) {
        return service.getOrderById(id);
    }

    // AUTOCOMPLETE ORDER
    public Call<List<String>> autocompleteOrders(String keyword, Integer shopId) {
        return service.autocompleteOrders(keyword, shopId);
    }
}