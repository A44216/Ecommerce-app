package com.example.ecommerceapp.data.repository.admin.user;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminUserService;
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;

import retrofit2.Call;

public class AdminUserRepository {

    private final AdminUserService service;

    public AdminUserRepository(TokenManager tokenManager) {
        this.service = ApiClient.getAdminUserService(tokenManager);
    }

    public Call<PageResponse<AdminUserResponse>> getUsers(int page, int size, Role role, UserStatus status, String keyword) {
        return service.getUsers(page, size, role, status, keyword);
    }
}
