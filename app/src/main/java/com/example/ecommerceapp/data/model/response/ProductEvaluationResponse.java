package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;

public class ProductEvaluationResponse {
    private Integer id;
    private ProductBaseResponse product;

    private BigDecimal score;
    private BigDecimal soldScore;
    private BigDecimal ratingScore;
    private BigDecimal priceScore;

    private String type;
    private String reason;

    public ProductEvaluationResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductBaseResponse getProduct() {
        return product;
    }

    public void setProduct(ProductBaseResponse product) {
        this.product = product;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getSoldScore() {
        return soldScore;
    }

    public void setSoldScore(BigDecimal soldScore) {
        this.soldScore = soldScore;
    }

    public BigDecimal getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(BigDecimal ratingScore) {
        this.ratingScore = ratingScore;
    }

    public BigDecimal getPriceScore() {
        return priceScore;
    }

    public void setPriceScore(BigDecimal priceScore) {
        this.priceScore = priceScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}