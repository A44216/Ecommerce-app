package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerProductViewModel;

public class SellerProductViewModelFactory implements ViewModelProvider.Factory {

    private final ProductRepository repository;

    public SellerProductViewModelFactory(ProductRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SellerProductViewModel(repository);
    }
}
