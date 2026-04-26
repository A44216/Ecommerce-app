package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.UserProductResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserProductService {
    @GET("products")
    Call<List<UserProductResponse>> getProducts();

    @GET("products/page")
    Call<com.example.ecommerceapp.data.model.response.PageResponse<UserProductResponse>> getProductsPaginated(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy
    );

    @GET("products/{id}")
    Call<UserProductResponse> getProductById(@Path("id") int id);

    @GET("products/category/{categoryId}")
    Call<List<UserProductResponse>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("products/suggest")
    Call<List<String>> suggestProducts(@Query("keyword") String keyword);

    @GET("products/trending")
    Call<List<UserProductResponse>> getTrendingProducts();

    @GET("products/search")
    Call<List<UserProductResponse>> searchProducts(
            @Query("keyword") String keyword,
            @Query("shopId") Integer shopId
    );

    @GET("products/search/page")
    Call<com.example.ecommerceapp.data.model.response.PageResponse<UserProductResponse>> searchProductsPaginated(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy
    );

    @GET("products/category/{categoryId}/page")
    Call<com.example.ecommerceapp.data.model.response.PageResponse<UserProductResponse>> getProductsByCategoryPaginated(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy
    );
}