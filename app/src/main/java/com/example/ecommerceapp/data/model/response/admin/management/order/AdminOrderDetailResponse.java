package com.example.ecommerceapp.data.model.response.admin.management.order;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderDetailResponse {

    private Integer id;
    private String orderCode;
    private Integer userId;
    private String username;
    private Integer shopId;
    private String shopName;

    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal platformFeeRate;
    private BigDecimal platformFeeAmount;
    private BigDecimal totalPrice;

    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private List<AdminOrderItemResponse> items;

    public Integer getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }

    public OrderStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getPlatformFeeRate() { return platformFeeRate; }
    public BigDecimal getPlatformFeeAmount() { return platformFeeAmount; }
    public BigDecimal getTotalPrice() { return totalPrice; }

    public String getShippingName() { return shippingName; }
    public String getShippingPhone() { return shippingPhone; }
    public String getShippingAddress() { return shippingAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }

    public List<AdminOrderItemResponse> getItems() { return items; }
}