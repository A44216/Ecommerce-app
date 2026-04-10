package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


import com.example.ecommerceapp.data.model.request.UserUpdateRequest;
import retrofit2.http.Body;
import retrofit2.http.PUT;
public interface UserService {

    @GET("users")
    Call<List<UserResponse>> getUsers();


    @GET("users/{id}")
    Call<UserProfileResponse> getUserProfile(@Path("id") long id);

    @PUT("users/{id}")
    Call<UserProfileResponse> updateUserProfile(@Path("id") long id, @Body UserUpdateRequest request);
}