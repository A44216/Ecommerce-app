package com.example.ecommerceapp.ui.viewmodel.seller.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductViewModel extends ViewModel {

    private final MutableLiveData<List<SellerProductResponse>> productList = new MutableLiveData<>();
    private final SellerProductRepository repository;

    public SellerProductViewModel(SellerProductRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<SellerProductResponse>> getProducts() {
        return productList;
    }

    public void fetchProducts() {
        repository.getProducts().enqueue(new Callback<List<SellerProductResponse>>() {
            @Override
            public void onResponse(Call<List<SellerProductResponse>> call, Response<List<SellerProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SellerProductResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void fetchProductsByShop(int shopId) {
        repository.getProductsByShop(shopId).enqueue(new Callback<List<SellerProductResponse>>() {
            @Override
            public void onResponse(Call<List<SellerProductResponse>> call, Response<List<SellerProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<SellerProductResponse>> call, Throwable t) {
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

    public void searchProducts(String keyword, Integer shopId) {

        repository.search(keyword, shopId)
                .enqueue(new Callback<List<SellerProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<SellerProductResponse>> call,
                                           Response<List<SellerProductResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            productList.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SellerProductResponse>> call, Throwable t) {
                        Log.e("SEARCH", t.getMessage());
                    }
                });
    }

}
