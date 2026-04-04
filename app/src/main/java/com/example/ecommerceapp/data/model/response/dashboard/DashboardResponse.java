package com.example.ecommerceapp.data.model.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    // ===== KPI =====
    private BigDecimal revenue;   // tvRevenue
    private Integer orders;       // tvOrders
    private Integer sold;         // tvSold

    // ===== LIST BEST SELLING =====
    private List<TopSellingProductResponse> topProductsByRevenue;
    private List<TopSellingProductResponse> topProductsBySold;

    // getters
    public BigDecimal getRevenue() {
        return revenue;
    }

    public Integer getOrders() {
        return orders;
    }

    public Integer getSold() {
        return sold;
    }

    public List<TopSellingProductResponse> getTopProductsByRevenue() {
        return topProductsByRevenue;
    }
    public List<TopSellingProductResponse> getTopProductsBySold() {
        return topProductsBySold;
    }

    // setters
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public void setTopProductsByRevenue(List<TopSellingProductResponse> topProductsByRevenue) {
        this.topProductsByRevenue = topProductsByRevenue;
    }

    public void setTopProductsBySold(List<TopSellingProductResponse> topProductsBySold) {
        this.topProductsBySold = topProductsBySold;
    }

}
