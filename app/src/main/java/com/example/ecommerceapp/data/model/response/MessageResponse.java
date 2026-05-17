package com.example.ecommerceapp.data.model.response;

public class MessageResponse {
    private Integer id;
    private Integer conversationId;
    private Integer senderId;
    private String senderName;
    private String message;
    private String createdAt;
    private Boolean isRead;
    private Boolean isAiGenerated;
    private Boolean isAiChat;

    public Integer getId() { return id; }
    public Integer getConversationId() { return conversationId; }
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public Boolean getIsRead() { return isRead; }
    public Boolean getIsAiGenerated() { return isAiGenerated; }
    public void setIsAiGenerated(Boolean isAiGenerated) { this.isAiGenerated = isAiGenerated; }
    public Boolean getIsAiChat() { return isAiChat; }
    public void setIsAiChat(Boolean isAiChat) { this.isAiChat = isAiChat; }
}