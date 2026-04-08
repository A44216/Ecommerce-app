package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.viewmodel.SellerProductViewModel;

public class SellerProductViewModelFactory implements ViewModelProvider.Factory {

    private final SellerProductRepository repository;

    public SellerProductViewModelFactory(SellerProductRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SellerProductViewModel(repository);
    }
}
