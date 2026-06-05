package com.example.ecommerceapp.data.model.response.admin.management.order;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderResponse {

    private Integer id;
    private String orderCode;

    private Integer userId;
    private String username;
    private String fullName;

    private Integer shopId;
    private String shopName;

    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal totalPrice;
    private BigDecimal platformFeeAmount;

    private LocalDateTime createdAt;
    
    private String imageOrder;

    public Integer getId() { return id; }
    public String getOrderCode() { return orderCode; }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }

    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }

    public OrderStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getPlatformFeeAmount() { return platformFeeAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public String getImageOrder() { return imageOrder; }
}