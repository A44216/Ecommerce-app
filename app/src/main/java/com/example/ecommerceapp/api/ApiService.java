package com.example.ecommerceapp.api;

import com.example.ecommerceapp.data.model.response.AddressResponse;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("users")
    Call<List<UserResponse>> getUsers();

    @GET("products")
    Call<List<ProductResponse>> getProducts();

    @GET("categories")
    Call<List<CategoryResponse>> getCategories();

    @GET("addresses")
    Call<List<AddressResponse>> getAddresses();


}