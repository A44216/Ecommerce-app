package com.example.ecommerceapp.data.model.request;

public class ConversationRequest {
    private Integer shopId;
    private Integer customerId;

    public ConversationRequest(Integer shopId, Integer customerId) {
        this.shopId = shopId;
        this.customerId = customerId;
    }
    public Integer getShopId() { return shopId; }
}