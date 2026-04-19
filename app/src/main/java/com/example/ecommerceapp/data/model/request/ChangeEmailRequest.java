package com.example.ecommerceapp.data.model.request;

public class ChangeEmailRequest {
    public String newEmail;
    public String oldEmailOtp;
    public String newEmailOtp;

    public ChangeEmailRequest(String newEmail, String oldEmailOtp, String newEmailOtp) {
        this.newEmail = newEmail;
        this.oldEmailOtp = oldEmailOtp;
        this.newEmailOtp = newEmailOtp;
    }
}
