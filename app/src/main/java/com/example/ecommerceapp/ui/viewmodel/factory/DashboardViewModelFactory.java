package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.DashboardRepository;
import com.example.ecommerceapp.ui.viewmodel.DashboardViewModel;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {
    private final DashboardRepository repository;

    public DashboardViewModelFactory(DashboardRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DashboardViewModel(repository);
    }
}
