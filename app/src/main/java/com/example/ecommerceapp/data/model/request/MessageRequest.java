package com.example.ecommerceapp.data.model.request;

public class MessageRequest {
    private Integer conversationId;
    private String message;
    private Integer senderId;

    public MessageRequest(Integer conversationId, String message, Integer senderId) {
        this.conversationId = conversationId;
        this.message = message;
        this.senderId = senderId;
    }
}