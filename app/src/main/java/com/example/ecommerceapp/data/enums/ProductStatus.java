package com.example.ecommerceapp.data.enums;

public enum ProductStatus {

    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối");

    private final String label;

    ProductStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}