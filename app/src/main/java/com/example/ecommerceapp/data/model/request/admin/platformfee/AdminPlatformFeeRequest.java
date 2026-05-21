package com.example.ecommerceapp.data.model.request.admin.platformfee;

import java.math.BigDecimal;

public class AdminPlatformFeeRequest {

    private BigDecimal rate;

    public AdminPlatformFeeRequest() {
    }

    public AdminPlatformFeeRequest(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
