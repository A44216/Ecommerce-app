package com.example.ecommerceapp.data.model.response.seller.product;

import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;

import java.math.BigDecimal;
import java.util.List;

public class SellerProductResponse {

    private Integer id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private int stock;
    private String description;
    private List<ProductImageResponse> images;
    private String categoryName;
    private String shopName;

    private BigDecimal ratingAvg;
    private int ratingCount;
    private int soldCount;

    private ProductStatus status;
    private Boolean isDeleted;

    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }

    public Integer getId() { return id; }
    public String getProductCode() {return productCode;}
    public int getStock() { return stock; }
    public String getCategoryName() { return categoryName; }
    public String getShopName() { return shopName; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getSoldCount() { return soldCount; }
    public ProductStatus getStatus() { return status; }
    public String getDescription() {
        return description;
    }
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public List<ProductImageResponse> getImages() {
        return images;
    }

}