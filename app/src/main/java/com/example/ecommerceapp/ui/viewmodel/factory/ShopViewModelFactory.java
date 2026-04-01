package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.ShopRepository;
import com.example.ecommerceapp.ui.viewmodel.ShopViewModel;

public class ShopViewModelFactory implements ViewModelProvider.Factory {

    private final ShopRepository repository;

    public ShopViewModelFactory(ShopRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ShopViewModel(repository);
    }
}