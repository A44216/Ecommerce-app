package com.example.ecommerceapp.ui.activity.home.user.settings; // Đổi package theo project của bạn

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.user.editprofile.EditUserProfileActivity;
import com.example.ecommerceapp.ui.activity.home.user.help.HelpCenterActivity;
import com.example.ecommerceapp.ui.activity.home.user.settings.ChangePasswordActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);

        // Chuyển sang trang sửa Tên, SĐT, Avatar
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditUserProfileActivity.class));
        });

        // Chuyển sang trang Đổi Mật Khẩu
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        Button btnHelpCenter = findViewById(R.id.btnHelpCenter);
        btnHelpCenter.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpCenterActivity.class));
        });

        Button btnRegisterSeller = findViewById(R.id.btnRegisterSeller);
        com.example.ecommerceapp.data.local.TokenManager tokenManager = com.example.ecommerceapp.data.local.TokenManager.getInstance(this);
        
        if ("SELLER".equals(tokenManager.getRole())) {
            btnRegisterSeller.setText("Shop của tôi");
            btnRegisterSeller.setOnClickListener(v -> {
                startActivity(new Intent(this, com.example.ecommerceapp.ui.activity.home.SellerHomeActivity.class));
            });
        } else {
            btnRegisterSeller.setOnClickListener(v -> {
                checkShopStatusAndNavigate();
            });
        }
    }

    private void checkShopStatusAndNavigate() {
        com.example.ecommerceapp.data.local.TokenManager tokenManager = com.example.ecommerceapp.data.local.TokenManager.getInstance(this);
        long userId = tokenManager.getUserId();
        if (userId == -1) return;

        com.example.ecommerceapp.api.ApiClient.getPublicShopService(tokenManager).getShopByUser((int) userId)
                .enqueue(new retrofit2.Callback<com.example.ecommerceapp.data.model.response.ShopResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, retrofit2.Response<com.example.ecommerceapp.data.model.response.ShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.ecommerceapp.data.enums.ShopStatus status = response.body().getStatus();
                    if (status == com.example.ecommerceapp.data.enums.ShopStatus.PENDING) {
                        showCancelRegistrationDialog();
                    } else if (status == com.example.ecommerceapp.data.enums.ShopStatus.APPROVED) {
                        android.widget.Toast.makeText(SettingsActivity.this, "Bạn đã là người bán hàng! Vui lòng đăng nhập lại để cập nhật quyền.", android.widget.Toast.LENGTH_LONG).show();
                    } else if (status == com.example.ecommerceapp.data.enums.ShopStatus.REJECTED) {
                        android.widget.Toast.makeText(SettingsActivity.this, "Yêu cầu đăng ký Shop của bạn đã bị từ chối.", android.widget.Toast.LENGTH_LONG).show();
                    } else if (status == com.example.ecommerceapp.data.enums.ShopStatus.CANCELED) {
                        startActivity(new Intent(SettingsActivity.this, UserRegisterShopActivity.class));
                    } else if (status == com.example.ecommerceapp.data.enums.ShopStatus.BLOCKED) {
                        android.widget.Toast.makeText(SettingsActivity.this, "Shop của bạn đã bị khóa.", android.widget.Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Shop không tồn tại (404) -> Chuyển sang form đăng ký
                    startActivity(new Intent(SettingsActivity.this, UserRegisterShopActivity.class));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, Throwable t) {
                android.widget.Toast.makeText(SettingsActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelRegistrationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đang chờ duyệt")
                .setMessage("Yêu cầu đăng ký Shop của bạn đang được duyệt. Bạn có muốn hủy yêu cầu này không?")
                .setPositiveButton("Hủy yêu cầu", (dialog, which) -> {
                    cancelRegistration();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelRegistration() {
        com.example.ecommerceapp.data.local.TokenManager tokenManager = com.example.ecommerceapp.data.local.TokenManager.getInstance(this);
        com.example.ecommerceapp.api.service.seller.SellerShopService api = com.example.ecommerceapp.api.ApiClient.getShopService(tokenManager);
        
        api.cancelRegistration().enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    android.widget.Toast.makeText(SettingsActivity.this, "Đã hủy yêu cầu đăng ký Shop thành công.", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(SettingsActivity.this, "Lỗi khi hủy yêu cầu.", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                android.widget.Toast.makeText(SettingsActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}