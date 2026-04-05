package com.example.ecommerceapp.data.model.response.seller;

import java.math.BigDecimal;
import java.util.List;

public class SellerDashboardResponse {

    // ===== KPI =====
    private BigDecimal revenue;   // tvRevenue
    private Integer orders;       // tvOrders
    private Integer sold;         // tvSold

    // ===== LIST BEST SELLING =====
    private List<SellerTopSellingProductResponse> topProductsByRevenue;
    private List<SellerTopSellingProductResponse> topProductsBySold;

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

    public List<SellerTopSellingProductResponse> getTopProductsByRevenue() {
        return topProductsByRevenue;
    }
    public List<SellerTopSellingProductResponse> getTopProductsBySold() {
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

    public void setTopProductsByRevenue(List<SellerTopSellingProductResponse> topProductsByRevenue) {
        this.topProductsByRevenue = topProductsByRevenue;
    }

    public void setTopProductsBySold(List<SellerTopSellingProductResponse> topProductsBySold) {
        this.topProductsBySold = topProductsBySold;
    }

}
