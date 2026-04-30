package com.example.ecommerceapp.data.model.response.seller.order;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;

public class SellerOrderResponse {
    private Integer orderId;
    private String orderCode;

    private OrderStatus status;

    private String customerName;
    private String customerPhone;

    private BigDecimal sellerRevenue;

    private String  createdAt;
    private String imageOrder;
    private PaymentStatus paymentStatus;

    public Integer getOrderId() {
        return orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public BigDecimal getSellerRevenue() {
        return sellerRevenue;
    }

    public String  getCreatedAt() {
        return createdAt;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getImageOrder() {
        return imageOrder;
    }

}
