package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;

public class UserOrderItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean isReviewed;
    private Integer reviewId;

    // Getters
    public Integer getId() { return id; }
    public Integer getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Boolean getIsReviewed() { return isReviewed; }
    public Integer getReviewId() { return reviewId; }

    // Setters
    public void setIsReviewed(Boolean isReviewed) { this.isReviewed = isReviewed; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
}