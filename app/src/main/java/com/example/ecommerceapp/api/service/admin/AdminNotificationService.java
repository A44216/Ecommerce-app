package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.model.request.SystemNotificationRequest;
import com.example.ecommerceapp.data.model.response.SystemNotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AdminNotificationService {

    @GET("admin/notifications")
    Call<List<SystemNotificationResponse>> getSystemNotifications();

    @POST("admin/notifications/broadcast")
    Call<Void> broadcastNotification(@Body SystemNotificationRequest request);
}
