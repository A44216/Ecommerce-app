package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserCategoryRepository;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;

public class UserHomeViewModelFactory implements ViewModelProvider.Factory {

    private final android.app.Application application;
    private final UserProductRepository productRepository;
    private final UserCategoryRepository categoryRepository;

    public UserHomeViewModelFactory(android.app.Application application, UserProductRepository productRepository, UserCategoryRepository categoryRepository) {
        this.application = application;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserHomeViewModel.class)) {
            return (T) new UserHomeViewModel(application, productRepository, categoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}