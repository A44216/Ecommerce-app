package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.data.model.request.auth.ResetPasswordRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnFinish;

    private AuthService authService;

    private String targetEmail; // Đổi tên biến cho rõ nghĩa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding = getResources().getDimensionPixelSize(R.dimen.screen_padding_large);
            v.setPadding(
                    padding + systemBars.left,
                    padding + systemBars.top,
                    padding + systemBars.right,
                    padding + systemBars.bottom
            );
            return insets;
        });

        // ĐÃ SỬA LỖI: Chìa khóa phải khớp 100% với ForgotPasswordActivity
        targetEmail = getIntent().getStringExtra("EMAIL_OR_USERNAME");

        if (targetEmail == null || targetEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi dữ liệu, vui lòng thử lại từ đầu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authService = ApiClient.getAuthService();

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
        ivBack.setOnClickListener(v -> finish());
        btnFinish.setOnClickListener(v -> handleResetPassword());
    }

    // Hàm xử lý reset password
    private void handleResetPassword() {
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

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

        // Đổi trạng thái nút tránh bấm 2 lần
        btnFinish.setEnabled(false);
        btnFinish.setText("Đang xử lý...");

        // Chuẩn bị Request
        ResetPasswordRequest request = new ResetPasswordRequest(targetEmail, newPassword);
        request.email = targetEmail;
        request.newPassword = newPassword;

        authService.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnFinish.setEnabled(true);
                btnFinish.setText("Hoàn tất");

                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // Về màn hình Login và xóa sạch lịch sử màn hình cũ
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String error = parseError(response);
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi từ Server: " + error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnFinish.setEnabled(true);
                btnFinish.setText("Hoàn tất");
                Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm đọc lỗi từ Backend
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() == null) return "UNKNOWN_ERROR";
            return response.errorBody().string().trim();
        } catch (Exception e) {
            return "PARSE_ERROR";
        }
    }
}