package com.example.ecommerceapp.data.model.assistant;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AssistantChatRequest {
    @SerializedName("history")
    private List<MessageContext> history;

    @SerializedName("message")
    private String message;

    public AssistantChatRequest(List<MessageContext> history, String message) {
        this.history = history;
        this.message = message;
    }

    public List<MessageContext> getHistory() {
        return history;
    }

    public void setHistory(List<MessageContext> history) {
        this.history = history;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
