package com.example.ecommerceapp.data.model.response.seller.shop;

import com.example.ecommerceapp.data.enums.ShopStatus;

import java.math.BigDecimal;

public class SellerShopResponse {

    private Integer id;
    private String shopName;
    private String description;
    private String ownerName;
    private ShopStatus status;
    private String createdAt;
    private String avatar;
    private String address;

    private BigDecimal ratingAvg;
    private Integer ratingCount;
    private String phone;
    private String email;

    // getters
    public Integer getId() { return id; }
    public String getShopName() { return shopName; }
    public String getDescription() { return description; }
    public String getOwnerName() { return ownerName; }
    public ShopStatus getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getAvatar() { return avatar; }
    public String getAddress() { return address; }

    public BigDecimal getRatingAvg() { return ratingAvg; }
    public Integer getRatingCount() { return ratingCount; }

    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // setters
    public void setId(Integer id) { this.id = id; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setDescription(String description) { this.description = description; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setStatus(ShopStatus status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setAddress(String address) { this.address = address; }

    public void setRatingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}