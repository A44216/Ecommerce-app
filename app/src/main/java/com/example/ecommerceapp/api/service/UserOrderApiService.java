package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserOrderApiService {
    @POST("orders")
    Call<Void> createOrder(@Body UserOrderRequest request);

    @GET("orders/user/{userId}")
    Call<List<UserOrderResponse>> getOrdersByUser(@Path("userId") int userId);

    @GET("orders/{id}")
    Call<UserOrderResponse> getOrderById(@Path("id") int orderId);
}