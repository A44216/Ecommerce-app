package com.example.ecommerceapp.data.model.ui;

import java.math.BigDecimal;

public class Order {
    public int id;
    public BigDecimal totalPrice;
    public BigDecimal subtotal;
    public BigDecimal discountAmount;
    public String status;
}
