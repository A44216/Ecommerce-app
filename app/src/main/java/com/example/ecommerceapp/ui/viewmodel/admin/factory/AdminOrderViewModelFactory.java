package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.AdminOrderRepository;
import com.example.ecommerceapp.data.repository.admin.AdminShopRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminOrderViewModel;

public class AdminOrderViewModelFactory implements ViewModelProvider.Factory {

    private final AdminOrderRepository orderRepository;
    private final AdminShopRepository shopRepository;

    public AdminOrderViewModelFactory(AdminOrderRepository orderRepository, AdminShopRepository shopRepository) {
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminOrderViewModel.class)) {
            return (T) new AdminOrderViewModel(orderRepository, shopRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
