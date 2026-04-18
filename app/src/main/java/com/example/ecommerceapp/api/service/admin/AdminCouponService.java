package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.model.request.admin.profile.AdminCouponRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface AdminCouponService {

    // LIST
    @GET("admin/coupons")
    Call<PageResponse<AdminCouponResponse>> getCoupons(
            @Query("page") int page,
            @Query("size") int size,
            @Query("status") CouponStatus status,
            @Query("keyword") String keyword,
            @Query("isDeleted") Boolean isDeleted
    );

    // DETAIL
    @GET("admin/coupons/{id}")
    Call<AdminCouponResponse> getCouponById(
            @Path("id") Integer id,
            @Query("isDeleted") Boolean isDeleted
    );

    // CREATE
    @POST("admin/coupons")
    Call<AdminCouponResponse> createCoupon(@Body AdminCouponRequest request);

    // UPDATE
    @PUT("admin/coupons/{id}")
    Call<AdminCouponResponse> updateCoupon(
            @Path("id") Integer id,
            @Body AdminCouponRequest request
    );

    // DELETE
    @DELETE("admin/coupons/{id}")
    Call<Void> deleteCoupon(@Path("id") Integer id);

    // DISABLE
    @PATCH("admin/coupons/{id}/disable")
    Call<Void> disableCoupon(@Path("id") Integer id);

    // ENABLE
    @PATCH("admin/coupons/{id}/enable")
    Call<Void> enableCoupon(@Path("id") Integer id);

    // RESTORE
    @PATCH("admin/coupons/{id}/restore")
    Call<Void> restoreCoupon(@Path("id") Integer id);
}