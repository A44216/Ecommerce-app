package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminShopDetailViewModel;

public class AdminShopDetailViewModelFactory implements ViewModelProvider.Factory {

    private final AdminShopRepository repository;

    public AdminShopDetailViewModelFactory(AdminShopRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminShopDetailViewModel.class)) {
            return (T) new AdminShopDetailViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
