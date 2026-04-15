package com.example.ecommerceapp.data.model.request.admin.profile;

public class AdminChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    public AdminChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
