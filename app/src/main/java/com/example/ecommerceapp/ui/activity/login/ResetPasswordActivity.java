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
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.request.ResetPasswordRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnFinish;

    private String email;

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

        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Thiếu email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
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

        ResetPasswordRequest request = new ResetPasswordRequest(email, newPassword);
        request.email = email;
        request.newPassword = newPassword;

        ApiService apiService = ApiClient.getPublicApiService();

        apiService.resetPassword(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(ResetPasswordActivity.this,
                            "Đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(ResetPasswordActivity.this,
                            "Reset thất bại",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this,
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

}