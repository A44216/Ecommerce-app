package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.response.PaymentResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaymentApiService {
    @GET("payment/create-payment")
    Call<PaymentResponse> createPaymentUrl(@Query("amount") long amount, @Query("orderInfo") String orderInfo);
}
