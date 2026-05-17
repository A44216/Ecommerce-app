package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.ConversationRequest;
import com.example.ecommerceapp.data.model.request.MessageRequest;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.data.model.response.MessageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApiService {
    @POST("conversations")
    Call<ConversationResponse> createConversation(@Body ConversationRequest request);

    @GET("messages/conversation/{conversationId}")
    Call<List<MessageResponse>> getMessages(@Path("conversationId") int conversationId, @retrofit2.http.Query("beforeTime") String beforeTime);

    @GET("messages/conversation/{conversationId}/ai-chat")
    Call<List<MessageResponse>> getAiChatMessages(@Path("conversationId") int conversationId, @retrofit2.http.Query("beforeTime") String beforeTime);

    @POST("messages")
    Call<MessageResponse> sendMessage(@Body MessageRequest request);

    @GET("conversations/customer/{customerId}")
    Call<List<ConversationResponse>> getConversationsByCustomer(@Path("customerId") int customerId);

    @GET("conversations/shop/{shopId}")
    Call<List<ConversationResponse>> getConversationsByShop(@Path("shopId") int shopId);

    @GET("messages/unread-count/customer/{customerId}")
    Call<Integer> getUnreadCountForCustomer(@Path("customerId") int customerId);

    @GET("messages/unread-count/shop/{shopId}")
    Call<Integer> getUnreadCountForShop(@Path("shopId") int shopId);

    @retrofit2.http.PUT("messages/read/{conversationId}")
    Call<Void> markAsRead(@Path("conversationId") int conversationId, @retrofit2.http.Query("userId") int userId);
}