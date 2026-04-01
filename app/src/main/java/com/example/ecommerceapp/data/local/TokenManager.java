package com.example.ecommerceapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "app_prefs";
    private static TokenManager instance;
    private final SharedPreferences prefs;

    private static final String KEY_TOKEN = "token";
    private static final String KEY_REMEMBER = "remember_login";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_SHOP_ID = "shop_id";

    private TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    public void setRememberLogin(boolean remember) {
        prefs.edit().putBoolean(KEY_REMEMBER, remember).apply();
    }

    public boolean isRememberLogin() {
        return prefs.getBoolean(KEY_REMEMBER, false);
    }

    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void saveUserId(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public void clearUserId() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    public long getShopId() {
        return prefs.getLong(KEY_SHOP_ID, 0);
    }
    public void saveShopId(long shopId) {
        prefs.edit().putLong(KEY_SHOP_ID, shopId).apply();
    }
    public void clearShopId() {
        prefs.edit().remove(KEY_SHOP_ID).apply();
    }

}