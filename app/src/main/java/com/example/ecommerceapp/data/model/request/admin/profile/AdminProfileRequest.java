package com.example.ecommerceapp.data.model.request.admin.profile;

public class AdminProfileRequest {

    private String fullName;
    private String email;
    private String phone;
    private String avatar;

    public AdminProfileRequest(String fullName, String email, String phone, String avatar) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }
}
