package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.request.admin.platformfee.AdminPlatformFeeRequest;
import com.example.ecommerceapp.data.model.response.admin.platformfee.AdminPlatformFeeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AdminPlatformFeeService {

    @POST("admin/platform-fees")
    Call<AdminPlatformFeeResponse> updateCurrentFee(
            @Body AdminPlatformFeeRequest request);
}
