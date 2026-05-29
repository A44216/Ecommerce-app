package com.example.ecommerceapp.data.model.assistant;

import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AssistantChatResponse {
    @SerializedName("type")
    private String type; // "TEXT" or "PRODUCT_CAROUSEL"

    @SerializedName("text")
    private String text;

    @SerializedName("data")
    private List<ProductResponse> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ProductResponse> getData() {
        return data;
    }

    public void setData(List<ProductResponse> data) {
        this.data = data;
    }
}
