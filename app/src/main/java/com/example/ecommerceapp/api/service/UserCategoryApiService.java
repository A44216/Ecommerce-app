package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserCategoryApiService {
    @GET("categories")
    Call<List<UserCategoryResponse>> getAllCategories();
}