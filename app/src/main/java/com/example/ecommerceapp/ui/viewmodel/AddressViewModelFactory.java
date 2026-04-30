package com.example.ecommerceapp.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.AddressRepository;

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