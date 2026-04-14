package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {

    // GET ALL + SEARCH (USER)
    @GET("categories")
    Call<List<CategoryResponse>> getCategories(
            @Query("keyword") String keyword
    );

    // GET BY ID (USER)
    @GET("categories/{id}")
    Call<CategoryResponse> getCategoryById(
            @Path("id") int id
    );
}