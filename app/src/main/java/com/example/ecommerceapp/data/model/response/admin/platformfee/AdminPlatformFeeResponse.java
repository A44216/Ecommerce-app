package com.example.ecommerceapp.data.model.response.admin.platformfee;

import java.math.BigDecimal;

public class AdminPlatformFeeResponse {

    private Integer id;
    private BigDecimal rate;
    private Boolean isActive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
