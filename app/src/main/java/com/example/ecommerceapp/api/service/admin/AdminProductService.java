package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.model.response.ProductAutocompleteResponse;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminProductDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminProductResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminProductService {

    @GET("admin/products")
    Call<PageResponse<AdminProductResponse>> getProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("shopId") Integer shopId,
            @Query("categoryId") Integer categoryId,
            @Query("status") ProductStatus status,
            @Query("isDeleted") Boolean isDeleted,
            @Query("keyword") String keyword,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );

    @GET("admin/products/{id}")
    Call<AdminProductDetailResponse> getProductById(
            @Path("id") Integer id
    );

    @GET("admin/products/autocomplete")
    Call<List<ProductAutocompleteResponse>> autocomplete(
            @Query("keyword") String keyword,
            @Query("shopId") Integer shopId
    );

    @PATCH("admin/products/{id}/status")
    Call<Void> updateProductStatus(
            @Path("id") Integer id,
            @Query("status") ProductStatus status
    );

    @PATCH("admin/products/{id}/delete")
    Call<Void> deleteProduct(
            @Path("id") Integer id
    );

    @PATCH("admin/products/{id}/restore")
    Call<Void> restoreProduct(
            @Path("id") Integer id
    );

}