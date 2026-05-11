package com.example.ecommerceapp.data.model.response;

public class MessageResponse {
    private Integer id;
    private Integer conversationId;
    private Integer senderId;
    private String senderName;
    private String message;
    private String createdAt;
    private Boolean isRead;

    public Integer getId() { return id; }
    public Integer getConversationId() { return conversationId; }
    public Integer getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public Boolean getIsRead() { return isRead; }
}