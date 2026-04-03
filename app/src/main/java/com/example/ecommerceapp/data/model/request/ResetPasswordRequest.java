package com.example.ecommerceapp.data.model.request;

public class ResetPasswordRequest {
    public String email;
    public String newPassword;

    public ResetPasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
}