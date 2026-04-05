package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;

public class UserHomeViewModelFactory implements ViewModelProvider.Factory {
    private final UserProductRepository repository;

    public UserHomeViewModelFactory(UserProductRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserHomeViewModel.class)) {
            return (T) new UserHomeViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}