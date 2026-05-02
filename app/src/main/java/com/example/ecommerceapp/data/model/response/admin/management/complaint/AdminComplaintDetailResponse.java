package com.example.ecommerceapp.data.model.response.admin.management.complaint;

import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserResponse;

import java.time.LocalDateTime;

public class AdminComplaintDetailResponse {

    private Integer id;
    private String complaintCode;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private AdminUserResponse resolvedBy;
    private String adminResponse;

    private AdminUserResponse user;

    public AdminComplaintDetailResponse() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComplaintCode() {
        return complaintCode;
    }

    public void setComplaintCode(String complaintCode) {
        this.complaintCode = complaintCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public AdminUserResponse getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(AdminUserResponse resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public AdminUserResponse getUser() {
        return user;
    }

    public void setUser(AdminUserResponse user) {
        this.user = user;
    }
}