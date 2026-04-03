package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.data.model.request.ShopRequest;
import com.example.ecommerceapp.data.model.response.ShopResponse;

import java.util.List;

import retrofit2.Call;

public class ShopRepository {

    private final ShopService apiService;

    public ShopRepository(ShopService apiService) {
        this.apiService = apiService;
    }

    public Call<List<ShopResponse>> getShops() {
        return apiService.getShops();
    }

    public Call<ShopResponse> getShopById(int id) {
        return apiService.getShopById(id);
    }

    public Call<ShopResponse> getShopByUser(int userId) {
        return apiService.getShopByUser(userId);
    }

    public Call<ShopResponse> createShop(ShopRequest request) {
        return apiService.createShop(request);
    }

    public Call<ShopResponse> updateShop(int id, ShopRequest request) {
        return apiService.updateShop(id, request);
    }

    public Call<Void> deleteShop(int id) {
        return apiService.deleteShop(id);
    }
}