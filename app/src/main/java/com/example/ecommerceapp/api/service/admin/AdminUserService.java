package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminUserService {

    // LIST USERS
    @GET("admin/users")
    Call<PageResponse<AdminUserResponse>> getUsers(
            @Query("page") int page,
            @Query("size") int size,
            @Query("role") Role role,
            @Query("status") UserStatus status,
            @Query("keyword") String keyword
    );

    // DETAIL
    @GET("admin/users/{id}")
    Call<AdminUserDetailResponse> getUserById(@Path("id") int id);

    // CHANGE STATUS
    @PATCH("admin/users/{id}/status")
    Call<Void> changeStatus(
            @Path("id") int id,
            @Query("status") UserStatus status
    );

    // UPDATE ROLE
    @PATCH("admin/users/{id}/role")
    Call<Void> updateRole(
            @Path("id") int id,
            @Query("role") Role role
    );

    @GET("admin/users/autocomplete")
    Call<List<String>> autocompleteUsers(
            @Query("keyword") String keyword
    );

}
