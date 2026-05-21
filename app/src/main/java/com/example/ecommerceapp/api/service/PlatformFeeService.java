package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.PlatformFeeResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PlatformFeeService {

    @GET("platform-fees/current")
    Call<PlatformFeeResponse> getCurrentFee();
}
