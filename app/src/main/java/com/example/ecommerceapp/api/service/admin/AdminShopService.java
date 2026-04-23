package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminShopService {

    @GET("admin/shops")
    Call<PageResponse<AdminShopResponse>> getShops(
            @Query("page") int page,
            @Query("size") int size,
            @Query("status") ShopStatus status,
            @Query("keyword") String keyword,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir
    );

    @GET("admin/shops/{id}")
    Call<AdminShopDetailResponse> getShopById(@Path("id") int id);

    @PATCH("admin/shops/{id}/status")
    Call<Void> updateShopStatus(
            @Path("id") int id,
            @Query("status") ShopStatus status
    );

    @GET("admin/shops/autocomplete")
    Call<List<String>> autocompleteShops(
            @Query("keyword") String keyword
    );

}
