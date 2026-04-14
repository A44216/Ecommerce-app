package com.example.ecommerceapp.data.model.response.seller.review;

import java.time.LocalDateTime;

public class SellerReviewResponse {

    // REVIEW ID
    private Integer reviewId;

    // USER INFO
    private String fullName;
    private String userAvatar;

    // REVIEW DATA
    private int rating;
    private String comment;
    private String createdAt;

    // SELLER REPLY
    private String sellerReply;
    private String sellerReplyAt;

    // STATUS
    private Boolean isReplied;

    public SellerReviewResponse() {
    }

    public Integer getReviewId() {
        return reviewId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public String  getSellerReplyAt() {
        return sellerReplyAt;
    }

    public Boolean getIsReplied() {
        return isReplied;
    }
}