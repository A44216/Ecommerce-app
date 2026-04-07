package com.example.ecommerceapp.data.model.request;

public class UserAddressRequest {
    private Integer userId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private Boolean isDefault;

    public UserAddressRequest(Integer userId, String fullName, String phone, String addressLine, String city, String district, String ward, Boolean isDefault) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.isDefault = isDefault;
    }
}