package com.example.ecommerceapp.ui.activity.home.user.settings; // Đổi package theo project của bạn

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.user.editprofile.EditUserProfileActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
    }
}