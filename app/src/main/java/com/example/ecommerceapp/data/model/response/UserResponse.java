package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;

public class UserResponse {

    private int id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
    private String createdAt;
    private String avatar;

    // Getter
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getAvatar() {
        return avatar;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}