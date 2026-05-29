package com.example.ecommerceapp.data.model.assistant;

import com.google.gson.annotations.SerializedName;

public class MessageContext {
    @SerializedName("role")
    private String role; // "user" or "assistant"

    @SerializedName("content")
    private String content;

    public MessageContext(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
