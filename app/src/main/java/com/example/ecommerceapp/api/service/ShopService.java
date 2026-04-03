package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ShopRequest;
import com.example.ecommerceapp.data.model.response.ShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ShopService {

    @GET("shops")
    Call<List<ShopResponse>> getShops();

    @GET("shops/{id}")
    Call<ShopResponse> getShopById(@Path("id") int id);

    @GET("shops/user/{userId}")
    Call<ShopResponse> getShopByUser(@Path("userId") int userId);

    @POST("shops")
    Call<ShopResponse> createShop(@Body ShopRequest request);

    @PUT("shops/{id}")
    Call<ShopResponse> updateShop(@Path("id") int id,
                                  @Body ShopRequest request);

    @DELETE("shops/{id}")
    Call<Void> deleteShop(@Path("id") int id);
}