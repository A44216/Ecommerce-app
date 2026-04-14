package com.example.ecommerceapp.data.model.response;

public class NotificationResponse {
    private Integer id;
    private String title;
    private String body;
    private String type;
    private Integer relatedId;
    private boolean read; // Trạng thái đã đọc hay chưa
    private String createdAt;

    public void setBody(String body) {
        this.body = body;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getType() { return type; }
    public Integer getRelatedId() { return relatedId; }
    public boolean isRead() { return read; }
    public String getCreatedAt() { return createdAt; }
}