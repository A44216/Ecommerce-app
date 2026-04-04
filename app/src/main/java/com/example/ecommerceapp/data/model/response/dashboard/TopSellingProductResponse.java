package com.example.ecommerceapp.data.model.response.dashboard;

import java.math.BigDecimal;

public class TopSellingProductResponse {

    private Integer productId;
    private String name;
    private Integer soldQuantity;
    private BigDecimal revenue;
    private String image;


    public Integer getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
