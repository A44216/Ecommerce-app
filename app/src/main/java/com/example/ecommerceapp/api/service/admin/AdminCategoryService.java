package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.profile.CategoryAdminResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface AdminCategoryService {

    @GET("admin/categories")
    Call<List<CategoryAdminResponse>> getAllCategories(
            @Query("isDeleted") Boolean isDeleted,
            @Query("keyword") String keyword
    );

    @POST("admin/categories")
    Call<CategoryAdminResponse> createCategory(@Body CategoryRequest request);

    @PUT("admin/categories/{id}")
    Call<CategoryAdminResponse> updateCategory(
            @Path("id") int id,
            @Body CategoryRequest request
    );

    @DELETE("admin/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @PUT("admin/categories/{id}/restore")
    Call<Void> restoreCategory(@Path("id") int id);
}