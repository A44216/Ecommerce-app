package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.request.seller.product.SellerProductRequest;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface SellerProductService {

    @GET("seller/products")
    Call<PageResponse<SellerProductResponse>> getProducts(
            @Query("status") String status,
            @Query("isDeleted") Boolean isDeleted,
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("seller/products/{id}")
    Call<SellerProductResponse> getProductById(@Path("id") int id);

    @DELETE("seller/products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

    @POST("seller/products")
    Call<SellerProductResponse> createProduct(@Body SellerProductRequest request);

    @PUT("seller/products/{id}")
    Call<SellerProductResponse> updateProduct(
            @Path("id") int id,
            @Body SellerProductRequest request
    );

    @PUT("seller/products/restore/{id}")
    Call<Void> restoreProduct(@Path("id") int id);

    @PUT("seller/products/submit/{id}")
    Call<Void> submitProduct(@Path("id") int id);
}