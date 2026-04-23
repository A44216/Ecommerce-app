package com.example.ecommerceapp.data.repository.admin;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminShopService;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopResponse;

import retrofit2.Call;

public class AdminShopRepository {

    private final AdminShopService service;

    public AdminShopRepository(TokenManager tokenManager) {
        this.service = ApiClient.getAdminShopService(tokenManager);
    }

    public Call<PageResponse<AdminShopResponse>> getShops(int page, int size, ShopStatus status, String keyword, String sortBy, String sortDir) {
        return service.getShops(page, size, status, keyword, sortBy, sortDir);
    }

    public Call<AdminShopDetailResponse> getShopById(int id) {
        return service.getShopById(id);
    }

    public Call<Void> updateShopStatus(int id, ShopStatus status) {
        return service.updateShopStatus(id, status);
    }
}
