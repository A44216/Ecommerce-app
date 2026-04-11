package com.example.ecommerceapp.ui.viewmodel.seller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductViewModel extends ViewModel {

    private final SellerProductRepository repository;

    public SellerProductViewModel(SellerProductRepository repository) {
        this.repository = repository;
    }

    private final MutableLiveData<PageResponse<SellerProductResponse>> productPage = new MutableLiveData<>();

    public LiveData<PageResponse<SellerProductResponse>> getProducts() {
        return productPage;
    }

    // GET ALL
    public void fetchProducts(int page, int size) {
        repository.getProducts(page, size)
                .enqueue(new Callback<PageResponse<SellerProductResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<SellerProductResponse>> call,
                                           Response<PageResponse<SellerProductResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            productPage.setValue(response.body());
                        } else {
                            Log.e("API", "ERROR = " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerProductResponse>> call, Throwable t) {
                        Log.e("API", "FAIL: " + t.getMessage());
                    }
                });
    }

    // DELETE
    public void deleteProduct(int id) {
        repository.deleteProduct(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            fetchProducts(0, 10); // reload
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("DELETE", t.getMessage());
                    }
                });
    }

    // SEARCH
    public void searchProducts(String keyword, int page, int size) {
        repository.search(keyword, page, size)
                .enqueue(new Callback<PageResponse<SellerProductResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<SellerProductResponse>> call,
                                           Response<PageResponse<SellerProductResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            productPage.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<SellerProductResponse>> call, Throwable t) {
                        Log.e("SEARCH", t.getMessage());
                    }
                });
    }
}
