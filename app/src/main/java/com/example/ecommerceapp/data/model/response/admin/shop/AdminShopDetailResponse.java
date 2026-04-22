package com.example.ecommerceapp.data.model.response.admin.shop;

import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserResponse;

import java.time.LocalDateTime;

public class AdminShopDetailResponse {

    private Integer id;
    private String shopName;
    private String description;
    private String email;
    private String phone;
    private String address;
    private ShopStatus status;
    private String avatar;
    private java.math.BigDecimal ratingAvg;
    private Integer ratingCount;
    private Integer totalProducts;
    private Integer totalOrders;
    private java.math.BigDecimal totalRevenue;
    private LocalDateTime createdAt;
    private AdminUserResponse owner;

    public AdminShopDetailResponse() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
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

    public AdminUserResponse getOwner() {
        return owner;
    }

    public void setOwner(AdminUserResponse owner) {
        this.owner = owner;
    }
}
