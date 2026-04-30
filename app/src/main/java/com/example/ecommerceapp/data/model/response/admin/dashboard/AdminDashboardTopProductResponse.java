package com.example.ecommerceapp.data.model.response.admin.dashboard;

import java.util.List;

public class AdminDashboardTopProductResponse {
    private List<AdminTopProductResponse> topByRevenue;
    private List<AdminTopProductResponse> topBySold;

    public List<AdminTopProductResponse> getTopByRevenue() { return topByRevenue; }
    public void setTopByRevenue(List<AdminTopProductResponse> topByRevenue) { this.topByRevenue = topByRevenue; }

    public List<AdminTopProductResponse> getTopBySold() { return topBySold; }
    public void setTopBySold(List<AdminTopProductResponse> topBySold) { this.topBySold = topBySold; }
}
