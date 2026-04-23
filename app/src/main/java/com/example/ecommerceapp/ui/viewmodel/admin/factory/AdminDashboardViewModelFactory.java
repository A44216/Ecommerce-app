package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.AdminDashboardRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminDashboardViewModel;

public class AdminDashboardViewModelFactory implements ViewModelProvider.Factory {
    private final AdminDashboardRepository repository;

    public AdminDashboardViewModelFactory(AdminDashboardRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminDashboardViewModel.class)) {
            return (T) new AdminDashboardViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
