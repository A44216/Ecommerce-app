package com.example.ecommerceapp.data.model.response.seller.dashboard;

import java.util.List;

public class SellerDashboardTopProductResponse {

    private List<SellerTopSellingProductResponse> topByRevenue;
    private List<SellerTopSellingProductResponse> topBySold;

    public List<SellerTopSellingProductResponse> getTopByRevenue() {
        return topByRevenue;
    }

    public void setTopByRevenue(List<SellerTopSellingProductResponse> topByRevenue) {
        this.topByRevenue = topByRevenue;
    }

    public List<SellerTopSellingProductResponse> getTopBySold() {
        return topBySold;
    }

    public void setTopBySold(List<SellerTopSellingProductResponse> topBySold) {
        this.topBySold = topBySold;
    }
}