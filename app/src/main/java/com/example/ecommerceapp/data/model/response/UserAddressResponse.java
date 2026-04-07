package com.example.ecommerceapp.data.model.response;

public class UserAddressResponse {
    private Integer id;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private Boolean isDefault;

    // Getters
    public Integer getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
    public Boolean getIsDefault() { return isDefault; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }
    public void setWard(String ward) { this.ward = ward; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    // Hàm tiện ích: Tự động ghép chuỗi địa chỉ đầy đủ
    public String getFullAddress() {
        return addressLine + ", " + ward + ", " + district + ", " + city;
    }
}