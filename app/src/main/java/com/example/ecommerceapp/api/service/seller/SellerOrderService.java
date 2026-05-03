package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SellerOrderService {

    // LIST ORDERS WITH FILTERS
    @GET("seller/orders")
    Call<PageResponse<SellerOrderResponse>> getOrders(
            @Query("status") String status,
            @Query("paymentMethod") String paymentMethod,
            @Query("paymentStatus") String paymentStatus,
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    // ORDER DETAIL
    @GET("seller/orders/{id}")
    Call<SellerOrderDetailResponse> getOrderDetail(
            @Path("id") int orderId
    );

    // UPDATE STATUS
    @PUT("seller/orders/{id}/status")
    Call<Void> updateOrderStatus(
            @Path("id") int orderId,
            @Query("status") String status
    );

    // AUTOCOMPLETE
    @GET("seller/orders/autocomplete")
    Call<List<String>> autocompleteOrders(
            @Query("keyword") String keyword
    );

    // ACCEPT RETURN
    @PUT("seller/orders/{id}/accept-return")
    Call<Void> acceptReturn(
            @Path("id") int orderId
    );

    // REJECT RETURN
    @PUT("seller/orders/{id}/reject-return")
    Call<Void> rejectReturn(
            @Path("id") int orderId,
            @Query("reason") String reason
    );
}