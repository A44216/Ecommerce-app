package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminCategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface AdminCategoryService {

    @GET("admin/categories")
    Call<List<AdminCategoryResponse>> getAllCategories(
            @Query("isDeleted") Boolean isDeleted,
            @Query("keyword") String keyword
    );

    @POST("admin/categories")
    Call<AdminCategoryResponse> createCategory(@Body CategoryRequest request);

    @PUT("admin/categories/{id}")
    Call<AdminCategoryResponse> updateCategory(
            @Path("id") int id,
            @Body CategoryRequest request
    );

    @DELETE("admin/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @PUT("admin/categories/{id}/restore")
    Call<Void> restoreCategory(@Path("id") int id);

    @GET("admin/categories/autocomplete")
    Call<List<AdminCategoryResponse>> autocompleteCategories(
            @Query("keyword") String keyword
    );

}