package com.example.ecommerceapp.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.CategoryRepository;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {

    private final CategoryRepository repository;

    public CategoryViewModelFactory(CategoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
