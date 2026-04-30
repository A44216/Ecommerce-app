package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.math.BigDecimal;

public class AdminTopShopResponse {
    private Integer id;
    private String shopName;
    private String avatar;
    private BigDecimal totalRevenue;
    private Integer totalOrders;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
}
