package com.example.ecommerceapp.data.model.response;

public class ReviewResponse {
    private int id;
    private String userName;
    private int productId;
    private int rating;
    private String comment;
    private String createdAt;

    // --- GETTERS ---
    public int getId() { return id; }
    public String getUserName() { return userName; }
    public int getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }

    // --- SETTERS ---
    public void setId(int id) { this.id = id; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}