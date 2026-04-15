package com.example.ecommerceapp.data.model.response;

import java.util.Collection;

public class UserProfileResponse {
    private Integer id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String avatar;


    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAvatar() { return avatar;}

    public Collection<Object> getPassword() {
        return null;
    }
    private boolean hasPassword;
    public boolean isHasPassword() {
        return hasPassword;
    }
}