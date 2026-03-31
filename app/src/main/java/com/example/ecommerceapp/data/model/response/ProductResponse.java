package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {

    private Integer id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String description;
    private List<String> images;
    private String categoryName;
    private String shopName;

    private BigDecimal ratingAvg;
    private int ratingCount;
    private int soldCount;

    private ProductStatus status;

    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }

    public Integer getId() { return id; }
    public int getStock() { return stock; }
    public String getCategoryName() { return categoryName; }
    public String getShopName() { return shopName; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getSoldCount() { return soldCount; }
    public ProductStatus getStatus() { return status; }
    public String getDescription() {
        return description;
    }
    public List<String> getImages() {
        return images;
    }

}