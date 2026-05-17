package com.example.ecommerceapp.data.model.request;

public class AiChatRequest {
    private int shopId;
    private int conversationId;
    private int senderId;
    private String message;

    public AiChatRequest(int shopId, int conversationId, int senderId, String message) {
        this.shopId = shopId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.message = message;
    }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }
    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
