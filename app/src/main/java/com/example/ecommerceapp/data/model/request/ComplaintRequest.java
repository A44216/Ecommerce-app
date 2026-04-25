package com.example.ecommerceapp.data.model.request;

public class ComplaintRequest {
    private int userId;
    private Integer orderId;
    private String content;

    public ComplaintRequest(int userId, Integer orderId, String content) {
        this.userId = userId;
        this.orderId = orderId;
        this.content = content;
    }
}
