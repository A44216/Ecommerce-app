package com.example.ecommerceapp.data.repository.admin.profile;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCouponService;
import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.profile.AdminCouponRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import retrofit2.Call;

public class AdminCouponRepository {

    private final AdminCouponService service;

    public AdminCouponRepository(TokenManager tokenManager) {
        this.service = ApiClient.getAdminCouponService(tokenManager);
    }

    // LIST
    public Call<PageResponse<AdminCouponResponse>> getCoupons(
            int page,
            int size,
            CouponStatus status,
            String keyword,
            Boolean isDeleted
    ) {
        return service.getCoupons(page, size, status, keyword, isDeleted);
    }

    // DETAIL
    public Call<AdminCouponResponse> getCouponById(Integer id) {
        return service.getCouponById(id);
    }

    // CREATE
    public Call<AdminCouponResponse> createCoupon(AdminCouponRequest request) {
        return service.createCoupon(request);
    }

    // UPDATE
    public Call<AdminCouponResponse> updateCoupon(Integer id, AdminCouponRequest request) {
        return service.updateCoupon(id, request);
    }

    // DELETE
    public Call<Void> deleteCoupon(Integer id) {
        return service.deleteCoupon(id);
    }

    // DISABLE
    public Call<Void> disableCoupon(Integer id) {
        return service.disableCoupon(id);
    }

    // ENABLE
    public Call<Void> enableCoupon(Integer id) {
        return service.enableCoupon(id);
    }

    // RESTORE
    public Call<Void> restoreCoupon(Integer id) {
        return service.restoreCoupon(id);
    }
}