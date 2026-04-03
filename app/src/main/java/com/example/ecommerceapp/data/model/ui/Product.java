package com.example.ecommerceapp.data.model.ui;

import java.math.BigDecimal;
import java.util.List;

public class Product {

    private int id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String description;
    private List<String> images;
    private String categoryName;
    private String shopName;

    // getter
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public List<String> getImages() { return images; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public void setStock(int stock) { this.stock = stock; }

    public void setDescription(String description) { this.description = description; }

    public void setImages(List<String> images) { this.images = images; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public void setShopName(String shopName) { this.shopName = shopName; }

}
