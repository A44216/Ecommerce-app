package com.example.ecommerceapp.api.service.seller;

import com.example.ecommerceapp.data.model.response.seller.SellerOrderDetailResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SellerOrderService {

    // LIST ORDERS BY STATUS (tab)
    @GET("seller/orders")
    Call<List<SellerOrderResponse>> getOrdersByStatus(
            @Query("status") String status
    );

    // ORDER DETAIL
    @GET("seller/orders/{id}")
    Call<SellerOrderDetailResponse> getOrderDetail(
            @Path("id") int orderId
    );
}
