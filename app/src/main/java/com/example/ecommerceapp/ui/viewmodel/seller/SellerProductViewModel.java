package com.example.ecommerceapp.ui.viewmodel.seller;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductViewModel extends ViewModel {

    private final SellerProductRepository repository;

    private String keyword = "";
    private String status = "";

    private final MutableLiveData<PageResponse<SellerProductResponse>> productPage =
            new MutableLiveData<>();

    public SellerProductViewModel(SellerProductRepository repository) {
        this.repository = repository;
    }

    public LiveData<PageResponse<SellerProductResponse>> getProducts() {
        return productPage;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void fetchProducts(int page, int size) {

        repository.getProducts(status, keyword, page, size)
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
                        Log.e("API", "FAIL: " + t.getMessage());
                    }
                });
    }


    public LiveData<SellerProductResponse> getProductById(int id) {

        MutableLiveData<SellerProductResponse> data = new MutableLiveData<>();

        repository.getProductById(id)
                .enqueue(new Callback<SellerProductResponse>() {
                    @Override
                    public void onResponse(Call<SellerProductResponse> call,
                                           Response<SellerProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<SellerProductResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }

    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();

    public void deleteProduct(int id) {

        repository.deleteProduct(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            deleteResult.setValue(true);
                        } else {
                            deleteResult.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        deleteResult.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

}