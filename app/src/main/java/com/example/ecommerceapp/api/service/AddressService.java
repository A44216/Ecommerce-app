package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.AddressResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AddressService {

    @GET("addresses")
    Call<List<AddressResponse>> getAddresses();
}