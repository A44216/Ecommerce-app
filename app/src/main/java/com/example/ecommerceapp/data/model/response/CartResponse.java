package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
    public int id;
    public BigDecimal totalPrice;
    public List<CartItemResponse> items;
}