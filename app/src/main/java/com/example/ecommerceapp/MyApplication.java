package com.example.ecommerceapp;

import android.app.Application;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.utils.CartManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Khởi tạo Giỏ hàng
        CartManager.init(this);
        
        // 2. Xử lý "Ghi nhớ đăng nhập"
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isRememberLogin()) {
            // Nếu người dùng KHÔNG tích "Ghi nhớ mật khẩu", chúng ta sẽ xóa sạch phiên đăng nhập 
            // mỗi khi ứng dụng bị tắt (khởi động lại process).
            tokenManager.clearAllData();
        }
    }
}
