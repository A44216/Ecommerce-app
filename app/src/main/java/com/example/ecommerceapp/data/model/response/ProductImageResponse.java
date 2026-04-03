package com.example.ecommerceapp.data.model.response;

public class ProductImageResponse {

    private Integer id;
    private String imageUrl;

    public ProductImageResponse() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}