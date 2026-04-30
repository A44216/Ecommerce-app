package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.math.BigDecimal;

public class AdminCategorySalesChartResponse {
    private String categoryName;
    private BigDecimal totalSales;

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
}
