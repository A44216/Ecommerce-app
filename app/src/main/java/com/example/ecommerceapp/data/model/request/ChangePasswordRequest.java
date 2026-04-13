package com.example.ecommerceapp.data.model.request;

public class ChangePasswordRequest {
    private String otpCode;
    private String newPassword;

    public ChangePasswordRequest(String otpCode, String newPassword) {
        this.otpCode = otpCode;
        this.newPassword = newPassword;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}