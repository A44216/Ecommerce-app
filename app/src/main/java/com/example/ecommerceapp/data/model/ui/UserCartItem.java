package com.example.ecommerceapp.data.model.ui;

public class UserCartItem {
    private Product product;
    private int quantity;
    private boolean isChecked; // Lưu trạng thái checkbox

    public UserCartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.isChecked = false;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}