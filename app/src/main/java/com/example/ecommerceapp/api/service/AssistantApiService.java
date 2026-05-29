package com.example.ecommerceapp.api.service;

import com.example.ecommerceapp.data.model.assistant.AssistantChatRequest;
import com.example.ecommerceapp.data.model.assistant.AssistantChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssistantApiService {

    @POST("v1/assistant/chat")
    Call<AssistantChatResponse> chatWithAssistant(@Body AssistantChatRequest request);

}
