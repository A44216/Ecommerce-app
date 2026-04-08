package com.example.ecommerceapp.data.model.request;

import com.example.ecommerceapp.data.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;

public class UserOrderRequest {
    private Integer addressId;
    private PaymentMethod paymentMethod;
    private Integer shopId;
    private Integer couponId;
    private Integer userId;
    private BigDecimal totalPrice;
    private List<OrderItemRequest> orderItems;

    // ĐÃ SỬA LẠI CONSTRUCTOR ĐỂ NHẬN THÊM orderItems
    public UserOrderRequest(Integer addressId, PaymentMethod paymentMethod, Integer userId, Integer shopId, BigDecimal totalPrice, List<OrderItemRequest> orderItems) {
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
        this.couponId = null;
    }

    // Getters and Setters...
    public Integer getAddressId() { return addressId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Integer getShopId() { return shopId; }
    public Integer getCouponId() { return couponId; }
    public Integer getUserId() { return userId; }
    public BigDecimal getTotalPrice() { return totalPrice; }

    public List<OrderItemRequest> getOrderItems() { return orderItems; }

    public static class OrderItemRequest {
        private Integer productId;
        private Integer quantity;
        private BigDecimal price;

        public OrderItemRequest(Integer productId, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
    }
}