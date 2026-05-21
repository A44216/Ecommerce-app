package com.example.ecommerceapp.data.repository.seller;

import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.model.request.seller.shop.SellerShopRequest;
import com.example.ecommerceapp.data.model.response.seller.shop.SellerShopResponse;

import retrofit2.Call;

import com.example.ecommerceapp.api.service.PlatformFeeService;
import com.example.ecommerceapp.data.model.response.PlatformFeeResponse;

public class SellerShopRepository {

    private final SellerShopService apiService;
    private final PlatformFeeService platformFeeService;

    public SellerShopRepository(SellerShopService apiService, PlatformFeeService platformFeeService) {
        this.apiService = apiService;
        this.platformFeeService = platformFeeService;
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

    public Call<Void> cancelRegistration() {
        return apiService.cancelRegistration();
    }

    public Call<com.example.ecommerceapp.data.model.response.ShopResponse> toggleAiReply(Integer id, Boolean enabled) {
        return apiService.toggleAiReply(id, enabled);
    }

    public Call<PlatformFeeResponse> getCurrentFee() {
        return platformFeeService.getCurrentFee();
    }
}