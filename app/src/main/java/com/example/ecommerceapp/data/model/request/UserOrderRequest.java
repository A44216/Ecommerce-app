package com.example.ecommerceapp.data.model.request;

import com.example.ecommerceapp.data.enums.PaymentMethod;
import java.math.BigDecimal;

public class UserOrderRequest {
    private Integer addressId;
    private PaymentMethod paymentMethod;
    private Integer shopId;
    private Integer couponId;
    private Integer userId;
    private BigDecimal totalPrice;

    public UserOrderRequest(Integer addressId, PaymentMethod paymentMethod, Integer userId, Integer shopId, BigDecimal totalPrice) {
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.couponId = null; // Tạm thời để null vì chưa có chức năng mã giảm giá
    }

    // Getters and Setters...
    public Integer getAddressId() { return addressId; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Integer getShopId() { return shopId; }
    public Integer getCouponId() { return couponId; }
    public Integer getUserId() { return userId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}