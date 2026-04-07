package com.example.ecommerceapp.data.model.response;

public class UserCategoryResponse {
    private Integer id;
    private String name;
    private String imageUrl;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}