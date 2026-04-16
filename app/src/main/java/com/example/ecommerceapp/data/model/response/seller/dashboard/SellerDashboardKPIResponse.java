package com.example.ecommerceapp.data.model.response.seller.dashboard;

import java.math.BigDecimal;

public class SellerDashboardKPIResponse {

    private BigDecimal revenue;
    private Integer orders;
    private Integer sold;

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }
}