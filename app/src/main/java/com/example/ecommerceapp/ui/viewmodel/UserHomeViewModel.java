package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeViewModel extends ViewModel {
    private final MutableLiveData<List<UserProductResponse>> productList = new MutableLiveData<>();
    private final UserProductRepository repository;

    public UserHomeViewModel(UserProductRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<UserProductResponse>> getProducts() {
        return productList;
    }

    public void fetchProducts() {
        repository.getProducts().enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}