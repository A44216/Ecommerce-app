package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.UserCategoryRepository;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;

public class UserHomeViewModelFactory implements ViewModelProvider.Factory {

    private final UserProductRepository productRepository;
    private final UserCategoryRepository categoryRepository; // Thêm dòng này

    // Cập nhật hàm tạo nhận 2 tham số
    public UserHomeViewModelFactory(UserProductRepository productRepository, UserCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserHomeViewModel.class)) {
            // Truyền 2 tham số vào ViewModel
            return (T) new UserHomeViewModel(productRepository, categoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}