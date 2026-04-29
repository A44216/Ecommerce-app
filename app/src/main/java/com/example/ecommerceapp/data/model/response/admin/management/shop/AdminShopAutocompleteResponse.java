package com.example.ecommerceapp.data.model.response.admin.management.shop;

import androidx.annotation.NonNull;

public class AdminShopAutocompleteResponse {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name != null ? name : "";
    }
}
