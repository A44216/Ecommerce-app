package com.example.ecommerceapp.data.model.response.seller;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public class SellerOrderDetailResponse {

    private Integer orderId;

    private OrderStatus status;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal totalPrice;

    private String createdAt;

    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;

    private String customerName;

    private List<SellerOrderItemResponse> items;

    public Integer getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getShippingName() {
        return shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<SellerOrderItemResponse> getItems() {
        return items;
    }

}
