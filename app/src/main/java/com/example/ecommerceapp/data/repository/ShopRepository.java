package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.model.request.seller.SellerShopRequest;
import com.example.ecommerceapp.data.model.response.seller.SellerShopResponse;

import retrofit2.Call;

public class ShopRepository {

    private final SellerShopService apiService;

    public ShopRepository(SellerShopService apiService) {
        this.apiService = apiService;
    }

    // lấy shop của seller hiện tại
    public Call<SellerShopResponse> getMyShop() {
        return apiService.getMyShop();
    }

    // tạo shop
    public Call<SellerShopResponse> createShop(SellerShopRequest request) {
        return apiService.createShop(request);
    }

    // update shop
    public Call<SellerShopResponse> updateShop(SellerShopRequest request) {
        return apiService.updateShop(request);
    }

    // update avatar
    public Call<SellerShopResponse> updateAvatar(String avatar) {
        return apiService.updateAvatar(avatar);
    }
}