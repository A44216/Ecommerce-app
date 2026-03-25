package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etUsernameOrEmail, etCode;
    private MaterialButton btnSendCode, btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

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
        });

        initViews();
        initEvents();
    }

    // Ánh xạ view
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etCode = findViewById(R.id.etCode);

        btnSendCode = findViewById(R.id.btnSendCode);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    // Thiết lập sự kiện
    private void initEvents() {

        // Nút quay lại màn Login
        ivBack.setOnClickListener(v -> finish());

        // Xử lý gửi mã OTP
        btnSendCode.setOnClickListener(v -> handleSendCode());

        // Xử lý xác nhận OTP
        btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    // Hàm xử lý gửi mã OTP
    private void handleSendCode() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();

        // Validate

        // 1. Không được để trống
        if (TextUtils.isEmpty(input)) {
            etUsernameOrEmail.setError("Không được để trống");
            etUsernameOrEmail.requestFocus();
            return;
        }

        // 2. Kiểm tra email nếu nhập email
        if (input.contains("@")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                etUsernameOrEmail.setError("Email không hợp lệ");
                etUsernameOrEmail.requestFocus();
                return;
            }
        } else {
            // 3. Username
            if (input.length() < 7) {
                etUsernameOrEmail.setError("Username phải >= 7 ký tự");
                etUsernameOrEmail.requestFocus();
                return;
            }
        }

        // TODO: gọi API gửi OTP ở đây

        Toast.makeText(this, "Đã gửi mã xác nhận", Toast.LENGTH_SHORT).show();
    }

    // Hàm xử lý xác nhận OTP
    private void handleConfirm() {
        String code = Objects.requireNonNull(etCode.getText()).toString().trim();

        // Validate

        // 1. Không được để trống
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Không được để trống");
            etCode.requestFocus();
            return;
        }

        // 2. OTP phải đủ 6 số
        if (code.length() != 6) {
            etCode.setError("Mã phải 6 số");
            etCode.requestFocus();
            return;
        }

        // TODO: gọi API verify OTP ở đây

        // Chuyển sang màn Reset Password
         Intent intent = new Intent(this, ResetPasswordActivity.class);
         startActivity(intent);
    }
}