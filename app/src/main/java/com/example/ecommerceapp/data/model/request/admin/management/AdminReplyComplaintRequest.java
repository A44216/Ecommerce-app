package com.example.ecommerceapp.data.model.request.admin.management;

public class AdminReplyComplaintRequest {
    private String response;

    public AdminReplyComplaintRequest(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
