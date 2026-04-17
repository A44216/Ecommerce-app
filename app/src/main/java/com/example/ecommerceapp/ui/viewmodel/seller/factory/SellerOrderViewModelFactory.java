package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.order.SellerOrderRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;

public class SellerOrderViewModelFactory implements ViewModelProvider.Factory {

    private final SellerOrderRepository repository;

    public SellerOrderViewModelFactory(SellerOrderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SellerOrderViewModel.class)) {
            return (T) new SellerOrderViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
