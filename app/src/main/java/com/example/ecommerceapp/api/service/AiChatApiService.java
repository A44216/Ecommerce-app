package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.request.AiChatRequest;
import com.example.ecommerceapp.data.model.response.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiChatApiService {
    @POST("ai-chat/ask")
    Call<MessageResponse> askAi(@Body AiChatRequest request);
}
