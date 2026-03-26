package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.AddressRepository;
import com.example.ecommerceapp.ui.viewmodel.AddressViewModel;

public class AddressViewModelFactory implements ViewModelProvider.Factory {

    private final AddressRepository repository;

    public AddressViewModelFactory(AddressRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddressViewModel(repository);
    }
}