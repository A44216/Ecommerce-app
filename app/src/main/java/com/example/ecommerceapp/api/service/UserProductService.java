package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.UserProductResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserProductService {
    @GET("products")
    Call<List<UserProductResponse>> getProducts();

    @GET("products/{id}")
    Call<UserProductResponse> getProductById(@Path("id") int id);
}