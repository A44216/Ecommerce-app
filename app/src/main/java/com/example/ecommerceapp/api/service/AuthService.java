package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.admin.profile.AdminChangePasswordRequest;
import com.example.ecommerceapp.data.model.request.auth.LoginRequest;
import com.example.ecommerceapp.data.model.request.auth.ResetPasswordRequest;
import com.example.ecommerceapp.data.model.request.UserRequest;
import com.example.ecommerceapp.data.model.request.auth.VerifyOtpRequest;
import com.example.ecommerceapp.data.model.request.auth.GoogleLoginRequest;
import com.example.ecommerceapp.data.model.request.auth.SendOtpRequest;
import com.example.ecommerceapp.data.model.response.auth.LoginResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/register")
    Call<UserResponse> register(@Body UserRequest request);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth/google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("auth/send-register-otp")
    Call<Void> sendRegisterOtp(@Body SendOtpRequest request);

    @POST("auth/send-forgot-password-otp")
    Call<Void> sendForgotPasswordOtp(@Body SendOtpRequest request);

    @POST("auth/verify-otp")
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth/change-password")
    Call<Map<String, String>> changePassword(@Body AdminChangePasswordRequest request);

}