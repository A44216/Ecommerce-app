package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserAddressApiService {
    @GET("addresses/user/{userId}")
    Call<List<UserAddressResponse>> getAddressesByUserId(@Path("userId") int userId);
}