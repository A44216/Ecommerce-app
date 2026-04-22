package com.example.ecommerceapp.data.model.response.admin.shop;

import com.example.ecommerceapp.data.enums.ShopStatus;

import java.time.LocalDateTime;

public class AdminShopResponse {

    private Integer id;
    private String shopName;
    private String email;
    private String phone;
    private ShopStatus status;
    private String avatar;
    private java.math.BigDecimal ratingAvg;
    private Integer totalOrders;
    private java.math.BigDecimal totalRevenue;
    private LocalDateTime createdAt;

    public AdminShopResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public java.math.BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(java.math.BigDecimal ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public java.math.BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(java.math.BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
