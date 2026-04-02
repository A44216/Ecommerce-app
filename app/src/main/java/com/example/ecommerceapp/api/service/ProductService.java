package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ProductRequest;
import com.example.ecommerceapp.data.model.response.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {

    @GET("products")
    Call<List<ProductResponse>> getProducts();

    @GET("products/{id}")
    Call<ProductResponse> getProductById(@Path("id") int id);

    @GET("products/shop/{shopId}")
    Call<List<ProductResponse>> getProductsByShop(@Path("shopId") int shopId);

    @GET("products/deleted")
    Call<List<ProductResponse>> getDeletedProducts();

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    @POST("products")
    Call<ProductResponse> createProduct(@Body ProductRequest request);

    @PUT("products/{id}")
    Call<ProductResponse> updateProduct(
            @Path("id") int id,
            @Body ProductRequest request
    );


}