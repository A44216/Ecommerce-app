package com.example.ecommerceapp.data.model.request;

public class ReviewRequest {
    private int userId;
    private int productId;
    private int rating;
    private String comment;

    public ReviewRequest(int userId, int productId, int rating, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    // Các hàm Getters
    public int getUserId() { return userId; }
    public int getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}