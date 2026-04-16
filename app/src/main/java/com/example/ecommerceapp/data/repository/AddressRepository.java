package com.example.ecommerceapp.data.repository;

import com.example.ecommerceapp.api.service.AddressService;
import com.example.ecommerceapp.data.model.response.AddressResponse;

import java.util.List;

import retrofit2.Call;

public class AddressRepository {

    private final AddressService apiService;

    public AddressRepository(AddressService apiService) {
        this.apiService = apiService;
    }

    public Call<List<AddressResponse>> getAddresses() {
        return apiService.getAddresses();
    }
}