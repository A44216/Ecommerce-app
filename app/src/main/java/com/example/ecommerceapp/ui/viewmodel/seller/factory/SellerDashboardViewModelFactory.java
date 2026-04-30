package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.seller.SellerDashboardRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerDashboardViewModel;

public class SellerDashboardViewModelFactory implements ViewModelProvider.Factory {
    private final SellerDashboardRepository repository;

    public SellerDashboardViewModelFactory(SellerDashboardRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SellerDashboardViewModel(repository);
    }
}
