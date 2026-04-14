package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface CategoryService {

    // GET ALL + SEARCH
    @GET("categories")
    Call<List<CategoryResponse>> getCategories(
            @Query("keyword") String keyword
    );

    // GET BY ID
    @GET("categories/{id}")
    Call<CategoryResponse> getCategoryById(
            @Path("id") int id
    );

    // CREATE CATEGORY
    @POST("categories")
    Call<CategoryResponse> createCategory(
            @Body CategoryRequest request
    );

    // UPDATE CATEGORY
    @PUT("categories/{id}")
    Call<CategoryResponse> updateCategory(
            @Path("id") int id,
            @Body CategoryRequest request
    );

    // DELETE CATEGORY
    @DELETE("categories/{id}")
    Call<Void> deleteCategory(
            @Path("id") int id
    );
}