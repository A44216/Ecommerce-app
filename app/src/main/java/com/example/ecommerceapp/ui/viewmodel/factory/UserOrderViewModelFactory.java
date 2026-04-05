package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserOrderRepository;
import com.example.ecommerceapp.ui.viewmodel.UserOrderViewModel;

public class UserOrderViewModelFactory implements ViewModelProvider.Factory {

    private final UserOrderRepository repository;

    public UserOrderViewModelFactory(UserOrderRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserOrderViewModel.class)) {
            return (T) new UserOrderViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}