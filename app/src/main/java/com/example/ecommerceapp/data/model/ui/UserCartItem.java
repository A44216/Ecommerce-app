package com.example.ecommerceapp.data.model.ui;

import com.example.ecommerceapp.data.model.response.UserProductResponse;

public class UserCartItem {
    private UserProductResponse product; // Đã đổi
    private int quantity;
    private boolean isChecked;

    public UserCartItem(UserProductResponse product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.isChecked = false;
    }

    public UserProductResponse getProduct() { return product; } // Đã đổi
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}