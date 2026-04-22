package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminShopViewModel;

public class AdminShopViewModelFactory implements ViewModelProvider.Factory {

    private final AdminShopRepository repository;

    public AdminShopViewModelFactory(AdminShopRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminShopViewModel.class)) {
            return (T) new AdminShopViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
