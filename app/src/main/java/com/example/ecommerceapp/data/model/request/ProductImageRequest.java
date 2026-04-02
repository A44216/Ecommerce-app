package com.example.ecommerceapp.data.model.request;

public class ProductImageRequest {

    private Integer productId;
    private String imageUrl;

    public ProductImageRequest() {}

    public ProductImageRequest(Integer productId, String imageUrl) {
        this.productId = productId;
        this.imageUrl = imageUrl;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}