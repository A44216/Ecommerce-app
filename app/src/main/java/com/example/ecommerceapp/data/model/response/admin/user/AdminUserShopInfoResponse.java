package com.example.ecommerceapp.data.model.response.admin.user;

import com.example.ecommerceapp.data.enums.ShopStatus;

import java.math.BigDecimal;

public class AdminUserShopInfoResponse {

    private Integer id;
    private String shopName;
    private ShopStatus status;
    private String description;

    private BigDecimal ratingAvg;
    private Integer totalOrders;
    private BigDecimal totalRevenue;

    public AdminUserShopInfoResponse() {
    }

    public AdminUserShopInfoResponse(Integer id, String shopName, ShopStatus status,
                                     String description, BigDecimal ratingAvg,
                                     Integer totalOrders, BigDecimal totalRevenue) {
        this.id = id;
        this.shopName = shopName;
        this.status = status;
        this.description = description;
        this.ratingAvg = ratingAvg;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
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

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(BigDecimal ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}