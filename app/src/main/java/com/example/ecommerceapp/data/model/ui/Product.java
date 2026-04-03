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

}
