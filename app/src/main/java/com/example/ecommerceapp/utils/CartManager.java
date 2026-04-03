package com.example.ecommerceapp.utils;

import com.example.ecommerceapp.data.model.ui.Product;
import com.example.ecommerceapp.data.model.ui.UserCartItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<UserCartItem> cartList;

    private CartManager() {
        cartList = new ArrayList<>();
    }

    // Design pattern Singleton: Đảm bảo toàn bộ app chỉ có 1 giỏ hàng duy nhất
    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Hàm Thêm vào giỏ
    public void addToCart(Product product) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa, nếu có thì tăng số lượng lên 1
        for (UserCartItem item : cartList) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        // Nếu chưa có thì thêm mới vào với số lượng là 1
        cartList.add(new UserCartItem(product, 1));
    }

    // Hàm Lấy danh sách giỏ hàng
    public List<UserCartItem> getCartItems() {
        return cartList;
    }
    // Hàm Xóa sản phẩm khỏi giỏ
    public void removeCartItem(UserCartItem item) {
        cartList.remove(item);
    }
}