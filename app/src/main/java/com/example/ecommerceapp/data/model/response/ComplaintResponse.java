package com.example.ecommerceapp.data.model.response;

public class ComplaintResponse {
    private int id;
    private Integer orderId;
    private String content;
    private String status;
    private String createdAt;

    public int getId() { return id; }
    public Integer getOrderId() { return orderId; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
