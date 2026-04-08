package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.seller.shop.SellerShopRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.viewmodel.SellerShopViewModel;

public class SellerShopViewModelFactory implements ViewModelProvider.Factory {

    private final SellerShopRepository repository;

    public SellerShopViewModelFactory(SellerShopRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SellerShopViewModel(repository);
    }
}