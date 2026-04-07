package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.request.seller.shop.SellerShopRequest;
import com.example.ecommerceapp.data.model.response.seller.shop.SellerShopResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface SellerShopService {

    @GET("seller/shops/me")
    Call<SellerShopResponse> getMyShop();

    @POST("seller/shops")
    Call<SellerShopResponse> createShop(@Body SellerShopRequest request);

    @PUT("seller/shops")
    Call<SellerShopResponse> updateShop(@Body SellerShopRequest request);

    @PATCH("seller/shops/avatar")
    Call<SellerShopResponse> updateAvatar(@Query("avatar") String avatar);
}