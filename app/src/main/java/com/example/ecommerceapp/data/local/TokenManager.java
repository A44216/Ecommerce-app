package com.example.ecommerceapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REMEMBER = "remember_login";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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

}