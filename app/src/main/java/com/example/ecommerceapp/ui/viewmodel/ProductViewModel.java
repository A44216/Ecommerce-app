package com.example.ecommerceapp.ui.viewmodel;

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

    private MutableLiveData<List<ProductResponse>> productList = new MutableLiveData<>();
    private ProductRepository repository = new ProductRepository();

    public LiveData<List<ProductResponse>> getProducts() {
        return productList;
    }

    public void fetchProducts() {
        repository.getProducts().enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                if (response.isSuccessful()) {
                    productList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
