package com.example.ecommerceapp.data.model.assistant;

import com.example.ecommerceapp.data.model.response.ProductResponse;
import java.util.List;

public class AssistantChatItem {
    public static final int VIEW_TYPE_USER = 1;
    public static final int VIEW_TYPE_AI_TEXT = 2;
    public static final int VIEW_TYPE_AI_CAROUSEL = 3;

    private int viewType;
    private String text;
    private List<ProductResponse> products;

    public AssistantChatItem(int viewType, String text, List<ProductResponse> products) {
        this.viewType = viewType;
        this.text = text;
        this.products = products;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
}
