package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.request.seller.product.SellerProductRequest;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SellerProductService {

    @GET("seller/products")
    Call<PageResponse<SellerProductResponse>> getProducts(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("seller/products/{id}")
    Call<SellerProductResponse> getProductById(@Path("id") int id);

    @GET("seller/products/deleted")
    Call<PageResponse<SellerProductResponse>> getDeletedProducts(
            @Query("page") int page,
            @Query("size") int size
    );

    @DELETE("seller/products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    @POST("seller/products")
    Call<SellerProductResponse> createProduct(@Body SellerProductRequest request);

    @PUT("seller/products/{id}")
    Call<SellerProductResponse> updateProduct(
            @Path("id") int id,
            @Body SellerProductRequest request
    );

    @GET("seller/products/search")
    Call<PageResponse<SellerProductResponse>> searchProducts(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );
}