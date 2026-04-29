package com.example.ecommerceapp.data.model.response.admin.management.product;

import com.example.ecommerceapp.data.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminProductResponse {
    private Integer id;
    private Integer shopId;
    private String shopName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;
    private Integer soldCount;
    private LocalDateTime createdAt;
    private Boolean isDeleted;
    private String image;

    public Integer getId() { return id; }
    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public ProductStatus getStatus() { return status; }
    public Integer getSoldCount() { return soldCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsDeleted() { return isDeleted; }
    public String getImage() { return image; }
    
    public void setStatus(ProductStatus status) { this.status = status; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public void setImage(String image) { this.image = image; }
}
