package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.AdminUserRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminUserDetailViewModel;

public class AdminUserDetailViewModelFactory implements ViewModelProvider.Factory {

    private final AdminUserRepository repository;

    public AdminUserDetailViewModelFactory(AdminUserRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminUserDetailViewModel.class)) {
            return (T) new AdminUserDetailViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
