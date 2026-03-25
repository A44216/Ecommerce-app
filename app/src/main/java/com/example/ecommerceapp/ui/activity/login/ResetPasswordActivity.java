package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnFinish;

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

        initViews();
        initEvents();

    }

    // Ánh xạ view
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnFinish = findViewById(R.id.btnFinish);
    }

    // Thiết lập sự kiện
    private void initEvents() {

        // Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý đổi mật khẩu
        btnFinish.setOnClickListener(v -> handleResetPassword());
    }

    // Hàm xử lý reset password
    private void handleResetPassword() {
        String newPassword = Objects.requireNonNull(etNewPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // Validate

        // 1. Không được để trống
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Không được để trống");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Không được để trống");
            etConfirmPassword.requestFocus();
            return;
        }

        // 2. Kiểm tra độ dài mật khẩu
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu phải >= 6 ký tự");
            etNewPassword.requestFocus();
            return;
        }

        // 3. Kiểm tra xác nhận mật khẩu
        if (!confirmPassword.equals(newPassword)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // TODO: gọi API update password ở đây

        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

        // Trở về màn hình Đăng nhập
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}