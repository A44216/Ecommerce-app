package com.example.ecommerceapp.data.model.response.dashboard;

import java.io.Serializable;
import java.math.BigDecimal;

public class RevenueChartResponse implements Serializable {
    private String date;
    private BigDecimal revenue;

    public String getLabel() { return date; }
    public BigDecimal getRevenue() { return revenue; }
}
