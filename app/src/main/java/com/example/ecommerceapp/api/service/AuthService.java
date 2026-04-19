package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.request.ResetPasswordRequest;
import com.example.ecommerceapp.data.model.request.UserRequest;
import com.example.ecommerceapp.data.model.request.VerifyOtpRequest;
import com.example.ecommerceapp.data.model.request.GoogleLoginRequest;
import com.example.ecommerceapp.data.model.request.SendOtpRequest;
import com.example.ecommerceapp.data.model.response.LoginResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

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

    @POST("auth/send-unlink-email-otp")
    Call<Void> sendUnlinkEmailOtp(@Body SendOtpRequest request);

    @POST("auth/send-verify-new-email-otp")
    Call<Void> sendVerifyNewEmailOtp(@Body SendOtpRequest request);
}