package com.example.ecommerceapp.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.repository.ProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<List<ProductResponse>> productList = new MutableLiveData<>();
    private final ProductRepository repository;

    public ProductViewModel(ProductRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ProductResponse>> getProducts() {
        return productList;
    }

    public void fetchProducts() {
        repository.getProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void fetchProductsByShop(int shopId) {
        repository.getProductsByShop(shopId).enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteProduct(int id, int shopId) {
        repository.deleteProduct(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchProductsByShop(shopId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DELETE", t.getMessage());
            }
        });
    }
}
