package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.math.BigDecimal;

public class AdminRevenueChartResponse {
    private String date;
    private BigDecimal revenue;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLabel() { return date; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
