package com.example.ecommerceapp.data.model.response.seller.order;

import java.math.BigDecimal;

public class SellerOrderItemResponse {

    private Integer productId;
    private String productCode;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;

    public Integer getProductId() {
        return productId;
    }


    public String getProductCode() { return productCode; }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}