package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {

    @GET("categories")
    Call<List<CategoryResponse>> getCategories();

    @GET("categories/shop/{shopId}")
    Call<List<CategoryResponse>> getCategoriesByShopId(@retrofit2.http.Path("shopId") int shopId);
}