package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.math.BigDecimal;

public class AdminRevenueChartResponse {
    private String label;
    private BigDecimal revenue;

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
