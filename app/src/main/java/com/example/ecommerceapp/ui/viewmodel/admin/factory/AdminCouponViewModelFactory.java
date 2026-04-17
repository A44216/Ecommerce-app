package com.example.ecommerceapp.ui.viewmodel.admin.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.admin.profile.AdminCouponRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCouponViewModel;

public class AdminCouponViewModelFactory implements ViewModelProvider.Factory {

    private final AdminCouponRepository repository;

    public AdminCouponViewModelFactory(AdminCouponRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(AdminCouponViewModel.class)) {
            return (T) new AdminCouponViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}