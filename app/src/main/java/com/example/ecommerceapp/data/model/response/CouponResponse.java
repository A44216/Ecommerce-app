package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;

public class CouponResponse {
    public Integer id;
    public String code;
    public Integer discountPercent;
    public BigDecimal discountAmount;
    public BigDecimal minOrderValue;
    public BigDecimal maxDiscountAmount;
}
