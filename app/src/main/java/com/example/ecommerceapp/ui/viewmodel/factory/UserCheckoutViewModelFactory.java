package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserAddressRepository;
import com.example.ecommerceapp.data.repository.UserOrderRepository;
import com.example.ecommerceapp.ui.viewmodel.UserCheckoutViewModel;

public class UserCheckoutViewModelFactory implements ViewModelProvider.Factory {

    private final UserOrderRepository orderRepository;
    private final UserAddressRepository addressRepository;

    public UserCheckoutViewModelFactory(UserOrderRepository orderRepository, UserAddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserCheckoutViewModel.class)) {
            return (T) new UserCheckoutViewModel(orderRepository, addressRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}