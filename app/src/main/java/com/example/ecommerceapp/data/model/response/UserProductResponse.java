package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.ProductStatus;
import java.math.BigDecimal;
import java.util.List;

public class UserProductResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String description;
    private List<UserProductImageResponse> images; // Dùng ImageResponse của User
    private String categoryName;
    private Integer shopId;
    private String shopName;
    private BigDecimal ratingAvg;
    private int soldCount;
    private ProductStatus status;

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public List<UserProductImageResponse> getImages() { return images; }
    public String getCategoryName() { return categoryName; }
    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getSoldCount() { return soldCount; }
    public ProductStatus getStatus() { return status; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImages(List<UserProductImageResponse> images) { this.images = images; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
}