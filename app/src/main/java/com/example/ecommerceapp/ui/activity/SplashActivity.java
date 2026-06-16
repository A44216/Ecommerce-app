package com.example.ecommerceapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.home.AdminHomeActivity;
import com.example.ecommerceapp.ui.activity.home.UserHomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvAppName = findViewById(R.id.tvAppName);

        // Hiệu ứng Fade-in mượt mà
        ivLogo.animate()
                .alpha(1f)
                .setDuration(1200)
                .start();

        tvAppName.animate()
                .alpha(1f)
                .setDuration(1200)
                .setStartDelay(400)
                .start();

        // Chờ 2.5 giây để hoàn tất hiệu ứng trước khi điều hướng
        new Handler(Looper.getMainLooper()).postDelayed(this::checkNavigation, 2500);
    }

    private void checkNavigation() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        String token = tokenManager.getToken();

        if (token != null) {
            if (tokenManager.isTokenExpired()) {
                tokenManager.logout();
                Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                startActivity(intent);
            } else {
                String role = tokenManager.getRole();
                Intent intent;
                if ("ADMIN".equals(role)) {
                    intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, UserHomeActivity.class);
                }
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(SplashActivity.this, UserHomeActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
