package com.example.ecommerceapp.data.model.ui;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String description;
    private String image;

    private String categoryName;
    private String shopName;

    // getter
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
}
