package com.example.ecommerceapp.data.model.response.seller.order;

import com.example.ecommerceapp.data.enums.OrderStatus;

import java.math.BigDecimal;

public class SellerOrderResponse {
    private Integer orderId;

    private OrderStatus status;

    private String customerName;

    private BigDecimal totalPrice;

    private String createdAt;

    private String imageOrder;

    public Integer getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImageOrder() {
        return imageOrder;
    }

}
