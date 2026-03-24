package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;

public class UserResponse {
    public int id;
    public String fullName;
    public String username;
    public String email;
    public String phone;
    public Role role;
    public UserStatus status;
    public String createdAt;
}