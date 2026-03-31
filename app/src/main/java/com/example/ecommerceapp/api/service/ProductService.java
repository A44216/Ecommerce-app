package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductService {

    @GET("products")
    Call<List<ProductResponse>> getProducts();

    @GET("products/shop/{shopId}")
    Call<List<ProductResponse>> getProductsByShop(@Path("shopId") int shopId);

    @GET("products/deleted")
    Call<List<ProductResponse>> getDeletedProducts();

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}