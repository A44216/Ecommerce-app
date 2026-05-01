package com.example.ecommerceapp.data.model.response;

public class SystemNotificationResponse {
    private Integer id;
    private String title;
    private String body;
    private String type;
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
