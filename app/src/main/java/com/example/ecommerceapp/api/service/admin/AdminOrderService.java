package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminOrderService {

    // GET LIST ORDERS (filter + paging)
    @GET("admin/orders")
    Call<PageResponse<AdminOrderResponse>> getOrders(
            @Query("page") int page,
            @Query("size") int size,
            @Query("keyword") String keyword,
            @Query("shopId") Integer shopId,
            @Query("status") String status,
            @Query("paymentMethod") String paymentMethod,
            @Query("paymentStatus") String paymentStatus,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );

    // GET ORDER DETAIL
    @GET("admin/orders/{id}")
    Call<AdminOrderDetailResponse> getOrderById(
            @Path("id") int id
    );

    // AUTOCOMPLETE ORDER (orderCode / phone / username)
    @GET("admin/orders/autocomplete")
    Call<List<String>> autocompleteOrders(
            @Query("keyword") String keyword,
            @Query("shopId") Integer shopId
    );
}