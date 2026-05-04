package com.example.ecommerceapp.data.model.request;

public class ProductEvaluationRequest {
    private Integer productId;

    public ProductEvaluationRequest(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}