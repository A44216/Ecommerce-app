package com.example.ecommerceapp.api;

import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.request.ResetPasswordRequest;
import com.example.ecommerceapp.data.model.request.UserRequest;
import com.example.ecommerceapp.data.model.response.AddressResponse;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.LoginResponse;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import com.example.ecommerceapp.data.model.request.GoogleLoginRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("users")
    Call<List<UserResponse>> getUsers();

    @GET("products")
    Call<List<ProductResponse>> getProducts();

    @GET("categories")
    Call<List<CategoryResponse>> getCategories();

    @GET("addresses")
    Call<List<AddressResponse>> getAddresses();

    @POST("auth/register")
    Call<UserResponse> register(@Body UserRequest request);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth/google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);
}
