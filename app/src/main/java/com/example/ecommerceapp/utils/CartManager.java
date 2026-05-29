package com.example.ecommerceapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<UserCartItem> cartList;
    private SharedPreferences prefs;
    private Gson gson;
    private Context context;

    private static final String PREF_NAME = "cart_prefs";
    private static final String KEY_CART_PREFIX = "cart_items_";

    private CartManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCart();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
    }

    public static CartManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CartManager is not initialized.");
        }
        return instance;
    }

    private String getCurrentCartKey() {
        long userId = TokenManager.getInstance(context).getUserId();
        return KEY_CART_PREFIX + userId;
    }

    public void loadCart() {
        String json = prefs.getString(getCurrentCartKey(), null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<UserCartItem>>() {}.getType();
            cartList = gson.fromJson(json, type);
        } else {
            cartList = new ArrayList<>();
        }
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
    }

    public void saveCart() {
        String json = gson.toJson(cartList);
        prefs.edit().putString(getCurrentCartKey(), json).apply();
    }

    public void mergeGuestCart() {
        long userId = TokenManager.getInstance(context).getUserId();
        if (userId == -1) return;

        String guestKey = KEY_CART_PREFIX + "-1";
        String guestJson = prefs.getString(guestKey, null);
        if (guestJson != null) {
            Type type = new TypeToken<ArrayList<UserCartItem>>() {}.getType();
            List<UserCartItem> guestCart = gson.fromJson(guestJson, type);
            if (guestCart != null) {
                for (UserCartItem item : guestCart) {
                    addToCart(item.getProduct(), item.getQuantity());
                }
            }
            prefs.edit().remove(guestKey).apply();
        }
    }

    public void addToCart(UserProductResponse product) {
        addToCart(product, 1);
    }

    public void addToCart(UserProductResponse product, int quantity) {
        for (UserCartItem item : cartList) {
            if (item.getProduct().getId() != null && product.getId() != null) {
                if (item.getProduct().getId().equals(product.getId())) {
                    item.setQuantity(item.getQuantity() + quantity);
                    saveCart();
                    return;
                }
            }
        }
        cartList.add(new UserCartItem(product, quantity));
        saveCart();
    }

    public List<UserCartItem> getCartItems() { return cartList; }
    
    public void removeCartItem(UserCartItem item) { 
        cartList.remove(item); 
        saveCart();
    }

    public int getTotalQuantity() {
        int total = 0;
        for (UserCartItem item : cartList) {
            total += item.getQuantity();
        }
        return total;
    }
    
    public void clearCart() {
        if (cartList != null) {
            cartList.clear();
            saveCart();
        }
    }
}