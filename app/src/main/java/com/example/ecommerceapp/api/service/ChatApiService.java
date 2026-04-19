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
    Call<List<MessageResponse>> getMessages(@Path("conversationId") int conversationId);

    @POST("messages")
    Call<MessageResponse> sendMessage(@Body MessageRequest request);

    @GET("conversations/customer/{customerId}")
    Call<List<ConversationResponse>> getConversationsByCustomer(@Path("customerId") int customerId);
}