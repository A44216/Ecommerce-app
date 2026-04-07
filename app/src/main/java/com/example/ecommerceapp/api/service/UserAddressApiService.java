package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.UserAddressRequest;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAddressApiService {
    @GET("addresses/user/{userId}")
    Call<List<UserAddressResponse>> getAddressesByUserId(@Path("userId") int userId);

    // Thêm hàm POST để tạo địa chỉ mới
    @POST("addresses")
    Call<UserAddressResponse> createAddress(@Body UserAddressRequest request);
}