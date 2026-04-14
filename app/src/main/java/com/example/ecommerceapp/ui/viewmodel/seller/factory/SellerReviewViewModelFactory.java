package com.example.ecommerceapp.ui.viewmodel.seller.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerReviewViewModel;

public class SellerReviewViewModelFactory implements ViewModelProvider.Factory {

    private final TokenManager tokenManager;

    public SellerReviewViewModelFactory(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SellerReviewViewModel.class)) {
            return (T) new SellerReviewViewModel(tokenManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}