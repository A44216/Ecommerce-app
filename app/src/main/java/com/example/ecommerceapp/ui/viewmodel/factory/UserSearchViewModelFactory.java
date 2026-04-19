package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.viewmodel.UserSearchViewModel;

public class UserSearchViewModelFactory implements ViewModelProvider.Factory {

    private final UserProductRepository productRepository;

    public UserSearchViewModelFactory(UserProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserSearchViewModel.class)) {
            return (T) new UserSearchViewModel(productRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
