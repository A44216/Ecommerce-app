package com.example.ecommerceapp.data.enums;

public enum PaymentMethod {

    COD("Thanh toán khi nhận hàng"),
    QR("Thanh toán QR");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
