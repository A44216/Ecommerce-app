package com.example.ecommerceapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ecommerceapp.data.model.assistant.AssistantChatItem;
import com.example.ecommerceapp.data.model.assistant.MessageContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AssistantHistoryManager {

    private static final String PREF_NAME = "AssistantHistoryPrefs";
    private static final String KEY_DISPLAY_ITEMS = "display_items";
    private static final String KEY_RAW_HISTORY = "raw_history";
    private static final String KEY_TIMESTAMP = "timestamp";

    // 24 hours in milliseconds
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public AssistantHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveHistory(long userId, List<AssistantChatItem> displayItems, List<MessageContext> rawHistory) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        String displayJson = gson.toJson(displayItems);
        String rawJson = gson.toJson(rawHistory);
        
        editor.putString(KEY_DISPLAY_ITEMS + "_" + userId, displayJson);
        editor.putString(KEY_RAW_HISTORY + "_" + userId, rawJson);
        editor.putLong(KEY_TIMESTAMP + "_" + userId, System.currentTimeMillis());
        editor.apply();
    }

    public boolean isHistoryValid(long userId) {
        long timestamp = sharedPreferences.getLong(KEY_TIMESTAMP + "_" + userId, 0);
        return (System.currentTimeMillis() - timestamp) < EXPIRATION_TIME;
    }

    public List<AssistantChatItem> getDisplayItems(long userId) {
        if (!isHistoryValid(userId)) return new ArrayList<>();
        
        String json = sharedPreferences.getString(KEY_DISPLAY_ITEMS + "_" + userId, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<AssistantChatItem>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public List<MessageContext> getRawHistory(long userId) {
        if (!isHistoryValid(userId)) return new ArrayList<>();
        
        String json = sharedPreferences.getString(KEY_RAW_HISTORY + "_" + userId, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<MessageContext>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void clearHistory(long userId) {
        sharedPreferences.edit()
                .remove(KEY_DISPLAY_ITEMS + "_" + userId)
                .remove(KEY_RAW_HISTORY + "_" + userId)
                .remove(KEY_TIMESTAMP + "_" + userId)
                .apply();
    }
}
