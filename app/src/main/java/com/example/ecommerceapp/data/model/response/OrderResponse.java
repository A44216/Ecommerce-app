package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;

public class OrderResponse {
    public int id;
    public OrderStatus status;
    public BigDecimal totalPrice;
    public PaymentMethod paymentMethod;
    public PaymentStatus paymentStatus;
}
