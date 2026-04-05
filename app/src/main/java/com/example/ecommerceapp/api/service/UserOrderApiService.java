package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserOrderApiService {
    @POST("orders")
    Call<Void> createOrder(@Body UserOrderRequest request);
}