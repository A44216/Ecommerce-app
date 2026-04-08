package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.AddressRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.viewmodel.SellerAddressViewModel;

public class SellerAddressViewModelFactory implements ViewModelProvider.Factory {

    private final AddressRepository repository;

    public SellerAddressViewModelFactory(AddressRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SellerAddressViewModel(repository);
    }
}