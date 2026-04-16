package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SellerCategoryService {

    // GET ALL + SEARCH (USER)
    @GET("seller/categories")
    Call<List<CategoryResponse>> getCategories(
            @Query("keyword") String keyword
    );

    // GET BY ID (USER)
    @GET("seller/categories/{id}")
    Call<CategoryResponse> getCategoryById(
            @Path("id") int id
    );
}