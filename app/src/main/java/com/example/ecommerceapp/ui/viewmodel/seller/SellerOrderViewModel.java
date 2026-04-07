package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;
import com.example.ecommerceapp.data.repository.OrderRepository;

import java.util.List;

public class SellerOrderViewModel extends ViewModel {

    private final OrderRepository repository;

    public SellerOrderViewModel(TokenManager tm) {
        repository = new OrderRepository(tm);
    }

    // ================= LIST =================
    public LiveData<List<SellerOrderResponse>> getOrders(String status, int shopId) {
        return repository.getOrders(status, shopId);
    }

    // ================= DETAIL =================
    public LiveData<SellerOrderDetailResponse> getOrderDetail(int orderId, int shopId) {
        return repository.getOrderDetail(orderId, shopId);
    }

    // ================= UPDATE STATUS =================
    public LiveData<Boolean> updateOrderStatus(int orderId, int shopId, OrderStatus status) {
        return repository.updateOrderStatus(orderId, shopId, status.name());
    }
}