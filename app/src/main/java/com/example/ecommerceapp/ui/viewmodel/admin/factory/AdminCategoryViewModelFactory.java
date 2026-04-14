package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.category.AdminCategoryRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCategoryViewModel;

public class AdminCategoryViewModelFactory implements ViewModelProvider.Factory {

    private final AdminCategoryRepository repository;

    public AdminCategoryViewModelFactory(AdminCategoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(AdminCategoryViewModel.class)) {
            return (T) new AdminCategoryViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
