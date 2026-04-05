package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;

public class UserOrderResponse {
    private Integer id;
    private Integer userId;
    private Integer shopId;
    private String status; // Trạng thái đơn hàng (PENDING, SHIPPING, DELIVERED...)
    private BigDecimal totalPrice;
    private String createdAt; // Ngày đặt hàng (Dùng String để Gson tự ép kiểu cho dễ)

    // Phần địa chỉ (để hiển thị nếu cần)
    private String shippingName;
    private String shippingPhone;
    private String addressLine;

    // Getters
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getShopId() { return shopId; }
    public String getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getCreatedAt() { return createdAt; }
    public String getShippingName() { return shippingName; }
    public String getShippingPhone() { return shippingPhone; }
    public String getAddressLine() { return addressLine; }
}