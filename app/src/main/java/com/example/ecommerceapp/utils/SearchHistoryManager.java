package com.example.ecommerceapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {

    private static final String PREF_NAME = "SearchHistoryPrefs";
    private static final String KEY_HISTORY = "search_history";
    private static final int MAX_HISTORY_SIZE = 10;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SearchHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<String> getSearchHistory() {
        String json = sharedPreferences.getString(KEY_HISTORY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void addSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        
        List<String> history = getSearchHistory();
        
        history.remove(keyword); // Remove if exists to move it to the top
        history.add(0, keyword); // Add to the beginning
        
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        saveHistory(history);
    }
    
    public void removeSearchKeyword(String keyword) {
        List<String> history = getSearchHistory();
        history.remove(keyword);
        saveHistory(history);
    }

    public void clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply();
    }

    private void saveHistory(List<String> history) {
        String json = gson.toJson(history);
        sharedPreferences.edit().putString(KEY_HISTORY, json).apply();
    }
}
