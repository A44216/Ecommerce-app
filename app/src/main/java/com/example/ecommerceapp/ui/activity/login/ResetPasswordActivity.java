package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // Lấy Insets của status bar + navigation bar
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int padding = getResources().getDimensionPixelSize(R.dimen.screen_padding_large);

            // Cộng padding cố định + Insets
            v.setPadding(
                    padding + systemBars.left,
                    padding + systemBars.top,
                    padding + systemBars.right,
                    padding + systemBars.bottom
            );

            return insets;
        });;

        ImageView ivBack = findViewById(R.id.ivBack);

        // Trở lại màn hình đăng nhập
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

    }
}