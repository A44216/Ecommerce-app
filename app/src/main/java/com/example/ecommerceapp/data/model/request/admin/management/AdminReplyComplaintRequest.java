package com.example.ecommerceapp.data.model.request.admin.management;

import com.example.ecommerceapp.data.enums.ComplaintStatus;

public class AdminReplyComplaintRequest {

    private ComplaintStatus status;
    private String response;

    public AdminReplyComplaintRequest(ComplaintStatus status, String response) {
        this.status = status;
        this.response = response;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}