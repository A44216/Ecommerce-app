package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.repository.UserCategoryRepository;
import com.example.ecommerceapp.data.repository.UserProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeViewModel extends ViewModel {
    // Các biến chứa dữ liệu
    private final MutableLiveData<List<UserProductResponse>> productList = new MutableLiveData<>();
    private final MutableLiveData<List<UserCategoryResponse>> categoryList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Kho chứa API
    private final UserProductRepository productRepository;
    private final UserCategoryRepository categoryRepository;

    // Sửa lại Constructor để nhận cả 2 Kho
    public UserHomeViewModel(UserProductRepository productRepository, UserCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Getters để giao diện (Activity/Fragment) lắng nghe
    public LiveData<List<UserProductResponse>> getProducts() { return productList; }
    public LiveData<List<UserCategoryResponse>> getCategoryList() { return categoryList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // ===========================================
    // 1. LẤY TẤT CẢ SẢN PHẨM (Hàm cũ của bạn)
    // ===========================================
    public void fetchProducts() {
        productRepository.getProducts().enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                t.printStackTrace();
                errorMessage.setValue("Lỗi tải sản phẩm: " + t.getMessage());
            }
        });
    }

    // ===========================================
    // 2. LẤY DANH SÁCH DANH MỤC
    // ===========================================
    public void fetchCategories() {
        categoryRepository.getAllCategories().enqueue(new Callback<List<UserCategoryResponse>>() {
            @Override
            public void onResponse(Call<List<UserCategoryResponse>> call, Response<List<UserCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserCategoryResponse>> call, Throwable t) {
                t.printStackTrace();
                errorMessage.setValue("Lỗi tải danh mục: " + t.getMessage());
            }
        });
    }

    // ===========================================
    // 3. LỌC SẢN PHẨM THEO DANH MỤC (Bấm vào Icon)
    // ===========================================
    public void fetchProductsByCategory(int categoryId) {
        productRepository.getProductsByCategory(categoryId).enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật lại chính danh sách productList để giao diện tự đổi
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                t.printStackTrace();
                errorMessage.setValue("Lỗi lọc sản phẩm: " + t.getMessage());
            }
        });
    }
}