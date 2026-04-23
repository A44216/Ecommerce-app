package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;

public class OrderResponse {
    public int id;
    public String orderCode;
    public OrderStatus status;
    public BigDecimal totalPrice;
    public BigDecimal subtotal;
    public BigDecimal discountAmount;
    public BigDecimal platformFeeRate;
    public BigDecimal platformFeeAmount;
    public Integer couponId;
    public PaymentMethod paymentMethod;
    public PaymentStatus paymentStatus;
}
