package com.example.ecommerceapp.data.model.request;

public class SystemNotificationRequest {
    private String title;
    private String body;
    private String type;

    public SystemNotificationRequest(String title, String body, String type) {
        this.title = title;
        this.body = body;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
