package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ChangePasswordRequest;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


import com.example.ecommerceapp.data.model.request.UserUpdateRequest;
import retrofit2.http.Body;
import retrofit2.http.PUT;
public interface UserService {

    @GET("users")
    Call<List<UserResponse>> getUsers();


    @GET("users/{id}")
    Call<UserProfileResponse> getUserProfile(@Path("id") long id);

    @PUT("users/{id}/profile")
    Call<UserProfileResponse> updateUserProfile(@Path("id") long id, @Body UserUpdateRequest request);

    @Multipart
    @POST("users/{id}/avatar")
    Call<UserProfileResponse> uploadAvatar(@Path("id") long id, @Part MultipartBody.Part file);

    @PUT("users/{id}/change-password")
    Call<Void> changePassword(@Path("id") long id, @Body ChangePasswordRequest request);
}