package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.CouponResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserCouponApiService {
    @GET("coupons/code/{code}")
    Call<CouponResponse> getCouponByCode(@Path("code") String code);
}
