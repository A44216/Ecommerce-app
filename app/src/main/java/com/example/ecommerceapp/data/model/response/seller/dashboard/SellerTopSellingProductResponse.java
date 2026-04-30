package com.example.ecommerceapp.data.model.response.seller.dashboard;

import java.math.BigDecimal;

public class SellerTopSellingProductResponse {

    private Integer productId;
    private String productCode;
    private String name;
    private Integer soldQuantity;
    private BigDecimal revenue;
    private String image;
    private BigDecimal price;

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductCode() { return productCode; }

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

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
