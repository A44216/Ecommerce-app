package com.example.ecommerceapp.data.model.ui; // Nhớ check lại package của bạn

public class Category {
    private int iconResId; // Tạm thời dùng icon có sẵn trong máy để test
    private String name;

    public Category(int iconResId, String name) {
        this.iconResId = iconResId;
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getName() {
        return name;
    }
}