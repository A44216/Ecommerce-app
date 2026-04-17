package com.example.ecommerceapp.data.model.response.seller.order;

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

    private BigDecimal sellerRevenue;

    private String createdAt;
    private String completedAt;

    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;

    private String customerName;

    private BigDecimal subtotal;

    private BigDecimal platformFeeRate;
    private BigDecimal platformFeeAmount;

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

    public BigDecimal getSellerRevenue() {
        return sellerRevenue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getPlatformFeeRate() {
        return platformFeeRate;
    }

    public BigDecimal getPlatformFeeAmount() {
        return platformFeeAmount;
    }

    public List<SellerOrderItemResponse> getItems() {
        return items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setSellerRevenue(BigDecimal sellerRevenue) {
        this.sellerRevenue = sellerRevenue;
    }

}
