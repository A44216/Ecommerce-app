package com.example.ecommerceapp.utils;

import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<UserCartItem> cartList;

    private CartManager() {
        cartList = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // ĐÃ ĐỔI SANG UserProductResponse
    public void addToCart(UserProductResponse product) {
        for (UserCartItem item : cartList) {
            // SO SÁNH BẰNG ID THAY VÌ NAME
            if (item.getProduct().getId() != null && product.getId() != null) {
                if (item.getProduct().getId().equals(product.getId())) {
                    item.setQuantity(item.getQuantity() + 1);
                    return;
                }
            }
        }
        cartList.add(new UserCartItem(product, 1));
    }


    public List<UserCartItem> getCartItems() { return cartList; }
    public void removeCartItem(UserCartItem item) { cartList.remove(item); }

    public int getTotalQuantity() {
        int total = 0;
        for (UserCartItem item : cartList) {
            total += item.getQuantity(); // Cộng dồn số lượng từng món
        }
        return total;
    }
}