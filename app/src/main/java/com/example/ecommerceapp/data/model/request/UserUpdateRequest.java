package com.example.ecommerceapp.data.model.request;

public class UserUpdateRequest {
    private String fullName;
    private String username;
    private String email;
    private String phone;

    public UserUpdateRequest(String fullName, String username, String email, String phone) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

}