package com.example.ecommerceapp;

import android.app.Application;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.utils.CartManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 1. Xử lý "Ghi nhớ đăng nhập" trước khi khởi tạo các Manager khác
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isRememberLogin()) {
            // Nếu người dùng KHÔNG tích "Ghi nhớ mật khẩu", chúng ta sẽ xóa sạch phiên đăng nhập 
            // mỗi khi ứng dụng bị tắt (khởi động lại process).
            tokenManager.clearAllData();
        }
        
        // 2. Khởi tạo Giỏ hàng (Sẽ load đúng giỏ hàng của Guest nếu vừa bị xóa token)
        CartManager.init(this);
    }
}
