package com.example.ecommerceapp.data.model.response.seller.order;

import java.time.LocalDateTime;

public class SellerReviewResponse {

    private Integer reviewId;
    private Integer productId;
    private String userName;
    private int rating;
    private String comment;
    private String sellerReply;
    private LocalDateTime sellerReplyAt;
    private LocalDateTime createdAt;

    public SellerReviewResponse() {
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public void setSellerReply(String sellerReply) {
        this.sellerReply = sellerReply;
    }

    public LocalDateTime getSellerReplyAt() {
        return sellerReplyAt;
    }

    public void setSellerReplyAt(LocalDateTime sellerReplyAt) {
        this.sellerReplyAt = sellerReplyAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}