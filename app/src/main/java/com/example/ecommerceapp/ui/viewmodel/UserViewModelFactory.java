package com.example.ecommerceapp.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository repository;

    public UserViewModelFactory(UserRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(repository);
    }
}
