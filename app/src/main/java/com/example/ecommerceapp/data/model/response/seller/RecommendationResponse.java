package com.example.ecommerceapp.data.model.response.seller;

import com.example.ecommerceapp.data.model.response.ProductBaseResponse;

import java.math.BigDecimal;

public class RecommendationResponse {
    private Integer id;
    private BigDecimal score;
    private String reason;
    private ProductBaseResponse product;

    public RecommendationResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ProductBaseResponse getProduct() {
        return product;
    }

    public void setProduct(ProductBaseResponse product) {
        this.product = product;
    }
}