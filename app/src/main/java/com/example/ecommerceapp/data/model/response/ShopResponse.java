package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.ShopStatus;

import java.time.LocalDateTime;

public class ShopResponse {

    private Integer id;
    private String shopName;
    private String description;
    private String ownerName;
    private ShopStatus status;
    private String createdAt;

    public Integer getId() { return id; }
    public String getShopName() { return shopName; }
    public String getDescription() { return description; }
    public String getOwnerName() { return ownerName; }
    public ShopStatus getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}