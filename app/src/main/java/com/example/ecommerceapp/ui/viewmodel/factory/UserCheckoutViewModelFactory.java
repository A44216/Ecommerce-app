package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserOrderRepository;
import com.example.ecommerceapp.ui.viewmodel.UserCheckoutViewModel;

public class UserCheckoutViewModelFactory implements ViewModelProvider.Factory {

    private final UserOrderRepository repository;

    public UserCheckoutViewModelFactory(UserOrderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserCheckoutViewModel.class)) {
            return (T) new UserCheckoutViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}