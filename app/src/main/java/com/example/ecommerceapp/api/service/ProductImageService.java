package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ProductImageRequest;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProductImageService {

    @Multipart
    @POST("/api/images/upload")
    Call<String> uploadImage(@Part MultipartBody.Part file);

    @POST("product-images")
    Call<ProductImageResponse> addProductImage(@Body ProductImageRequest request);

    @DELETE("product-images/{id}")
    Call<Void> deleteProductImage(@Path("id") int id);

}
