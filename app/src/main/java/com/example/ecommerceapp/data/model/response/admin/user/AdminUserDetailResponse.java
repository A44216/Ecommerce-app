package com.example.ecommerceapp.data.model.response.admin.user;

import com.example.ecommerceapp.data.enums.Provider;
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;


import java.time.LocalDateTime;

public class AdminUserDetailResponse {

    private Integer id;
    private String fullName;
    private String username;
    private String email;
    private String phone;

    private Role role;
    private UserStatus status;
    private String avatar;

    private Provider provider;
    private LocalDateTime createdAt;

    private AdminUserShopInfoResponse shop;

    public AdminUserDetailResponse() {
    }

    public AdminUserDetailResponse(Integer id, String fullName, String username, String email,
                                   String phone, Role role, UserStatus status,
                                   String avatar, Provider provider,
                                   LocalDateTime createdAt,
                                   AdminUserShopInfoResponse shop) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.avatar = avatar;
        this.provider = provider;
        this.createdAt = createdAt;
        this.shop = shop;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AdminUserShopInfoResponse getShop() {
        return shop;
    }

    public void setShop(AdminUserShopInfoResponse shop) {
        this.shop = shop;
    }
}