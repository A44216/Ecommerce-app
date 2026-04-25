package com.example.ecommerceapp.data.model.response;

import java.math.BigDecimal;
import java.util.List;

public class UserOrderResponse {
    private Integer id;
    private String orderCode;
    private Integer userId;
    private Integer shopId;
    private String status;
    private BigDecimal totalPrice;
    private String createdAt;
    private String paymentMethod;
    private String paymentStatus;

    // Phần địa chỉ (Dữ liệu phẳng)
    private String shippingName;
    private String shippingPhone;
    private String addressLine;

    // Danh sách sản phẩm
    private List<UserOrderItemResponse> orderItems;

    // --- GETTERS ---
    public Integer getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public Integer getUserId() { return userId; }
    public Integer getShopId() { return shopId; }
    public String getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getCreatedAt() { return createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getShippingName() { return shippingName; }
    public String getShippingPhone() { return shippingPhone; }
    public String getAddressLine() { return addressLine; }
    public List<UserOrderItemResponse> getOrderItems() { return orderItems; }

    // --- SETTERS (Thêm vào để Retrofit/Gson gán dữ liệu an toàn) ---
    public void setId(Integer id) { this.id = id; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setOrderItems(List<UserOrderItemResponse> orderItems) { this.orderItems = orderItems; }
}