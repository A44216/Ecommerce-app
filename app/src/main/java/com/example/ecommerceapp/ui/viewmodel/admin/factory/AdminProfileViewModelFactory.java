package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.AdminProfileRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminProfileViewModel;

public class AdminProfileViewModelFactory implements ViewModelProvider.Factory {

    private final AdminProfileRepository repo;

    public AdminProfileViewModelFactory(AdminProfileRepository repo) {
        this.repo = repo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(AdminProfileViewModel.class)) {
            return (T) new AdminProfileViewModel(repo);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
