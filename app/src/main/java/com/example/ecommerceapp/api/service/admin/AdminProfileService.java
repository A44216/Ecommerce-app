package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.request.admin.profile.AdminProfileRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface AdminProfileService {

    @GET("admin/profile")
    Call<AdminProfileResponse> getProfile();

    @PUT("admin/profile")
    Call<AdminProfileResponse> updateProfile(
            @Body AdminProfileRequest request
    );
}
