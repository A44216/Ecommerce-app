package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.UserAddressRequest;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

public interface UserAddressApiService {
    @GET("addresses/user/{userId}")
    Call<List<UserAddressResponse>> getAddressesByUserId(@Path("userId") int userId);

    // Thêm hàm POST để tạo địa chỉ mới
    @POST("addresses")
    Call<UserAddressResponse> createAddress(@Body UserAddressRequest request);

    @PUT("addresses/{id}")
    Call<UserAddressResponse> updateAddress(@Path("id") int id, @Body UserAddressRequest request);

    @DELETE("addresses/{id}")
    Call<Void> deleteAddress(@Path("id") int id);
}