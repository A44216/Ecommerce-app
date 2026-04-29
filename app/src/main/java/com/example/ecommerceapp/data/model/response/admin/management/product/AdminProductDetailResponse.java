package com.example.ecommerceapp.data.model.response.admin.management.product;

import com.example.ecommerceapp.data.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminProductDetailResponse {
    private Integer id;
    private String productCode;
    private Integer shopId;
    private String shopName;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private ProductStatus status;
    private BigDecimal ratingAvg;
    private Integer ratingCount;
    private Integer soldCount;
    private LocalDateTime createdAt;
    private List<String> images;
    private Boolean isDeleted;

    public Integer getId() { return id; }
    public String getProductCode() {return productCode;}
    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getDescription() { return description; }
    public ProductStatus getStatus() { return status; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public Integer getRatingCount() { return ratingCount; }
    public Integer getSoldCount() { return soldCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getImages() { return images; }
    public Boolean getIsDeleted() { return isDeleted; }
}
