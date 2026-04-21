package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.math.BigDecimal;

public class AdminDashboardKPIResponse {
    private Long totalUsers;
    private Long totalShops;
    private BigDecimal totalPlatformRevenue;
    private Long pendingComplaints;

    private Long pendingShops;
    private Long pendingProducts;
    private BigDecimal totalGMV;
    private Long totalOrders;
    private Long activeCoupons;

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getTotalShops() { return totalShops; }
    public void setTotalShops(Long totalShops) { this.totalShops = totalShops; }

    public BigDecimal getTotalPlatformRevenue() { return totalPlatformRevenue; }
    public void setTotalPlatformRevenue(BigDecimal totalPlatformRevenue) { this.totalPlatformRevenue = totalPlatformRevenue; }

    public Long getPendingComplaints() { return pendingComplaints; }
    public void setPendingComplaints(Long pendingComplaints) { this.pendingComplaints = pendingComplaints; }

    public Long getPendingShops() { return pendingShops; }
    public void setPendingShops(Long pendingShops) { this.pendingShops = pendingShops; }

    public Long getPendingProducts() { return pendingProducts; }
    public void setPendingProducts(Long pendingProducts) { this.pendingProducts = pendingProducts; }

    public BigDecimal getTotalGMV() { return totalGMV; }
    public void setTotalGMV(BigDecimal totalGMV) { this.totalGMV = totalGMV; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getActiveCoupons() { return activeCoupons; }
    public void setActiveCoupons(Long activeCoupons) { this.activeCoupons = activeCoupons; }
}
