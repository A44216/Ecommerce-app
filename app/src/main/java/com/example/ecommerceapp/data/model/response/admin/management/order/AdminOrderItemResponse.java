package com.example.ecommerceapp.data.model.response.admin.management.order;

import java.math.BigDecimal;

public class AdminOrderItemResponse {

    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}