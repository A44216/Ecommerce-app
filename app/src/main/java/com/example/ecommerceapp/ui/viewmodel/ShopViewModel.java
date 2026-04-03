package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.ShopResponse;
import com.example.ecommerceapp.data.repository.ShopRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopViewModel extends ViewModel {

    private final MutableLiveData<List<ShopResponse>> shopList = new MutableLiveData<>();
    private final ShopRepository repository;

    public ShopViewModel(ShopRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ShopResponse>> getShops() {
        return shopList;
    }

    public void fetchShops() {
        repository.getShops().enqueue(new Callback<List<ShopResponse>>() {
            @Override
            public void onResponse(Call<List<ShopResponse>> call, Response<List<ShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shopList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ShopResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void fetchShopByUser(int userId) {
        repository.getShopByUser(userId).enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // nếu bạn dùng LiveData 1 shop thì tạo thêm MutableLiveData<ShopResponse>
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteShop(int id) {
        repository.deleteShop(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchShops();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}