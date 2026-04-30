package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.seller.shop.SellerShopRequest;
import com.example.ecommerceapp.data.model.response.seller.shop.SellerShopResponse;
import com.example.ecommerceapp.data.repository.seller.SellerShopRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerShopViewModel extends ViewModel {

    private final MutableLiveData<SellerShopResponse> shop = new MutableLiveData<>();
    private final SellerShopRepository repository;

    public SellerShopViewModel(SellerShopRepository repository) {
        this.repository = repository;
    }

    public LiveData<SellerShopResponse> getShop() {
        return shop;
    }

    // GET MY SHOP
    public void fetchMyShop() {
        repository.getMyShop().enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // CREATE SHOP
    public void createShop(SellerShopRequest request) {
        repository.createShop(request).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // UPDATE SHOP
    public void updateShop(SellerShopRequest request) {
        repository.updateShop(request).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // UPDATE AVATAR
    public void updateAvatar(String avatar) {
        repository.updateAvatar(avatar).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}