package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.seller.SellerReviewRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerReviewViewModel;

public class SellerReviewViewModelFactory implements ViewModelProvider.Factory {

    private final SellerReviewRepository repository;

    public SellerReviewViewModelFactory(SellerReviewRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(SellerReviewViewModel.class)) {
            return (T) new SellerReviewViewModel(repository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}