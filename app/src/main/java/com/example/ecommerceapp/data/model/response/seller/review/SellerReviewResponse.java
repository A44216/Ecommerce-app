package com.example.ecommerceapp.data.model.response.seller.review;

import java.time.LocalDateTime;

public class SellerReviewResponse {

    private Integer reviewId;

    // PRODUCT INFO
    private Integer productId;
    private String productName;

    // USER INFO
    private Integer userId;
    private String fullName;
    private String userAvatar;

    // REVIEW DATA
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    // SELLER REPLY
    private String sellerReply;
    private LocalDateTime sellerReplyAt;
    private Boolean isReplied;

    public SellerReviewResponse() {
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public LocalDateTime getSellerReplyAt() {
        return sellerReplyAt;
    }

    public Boolean getIsReplied() {
        return isReplied;
    }
}