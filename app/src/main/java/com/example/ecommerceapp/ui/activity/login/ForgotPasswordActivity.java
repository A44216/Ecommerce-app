package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.ecommerceapp.data.model.request.auth.SendOtpRequest;
import com.example.ecommerceapp.data.model.request.auth.VerifyOtpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etUsernameOrEmail, etCode;
    private MaterialButton btnSendCode, btnConfirm;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

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

        // BỔ SUNG: Khởi tạo apiService
        authService = ApiClient.getAuthService();
        initViews();
        initEvents();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void initEvents() {
        ivBack.setOnClickListener(v -> finish());
        btnSendCode.setOnClickListener(v -> handleSendCode());
        btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    // --- HÀM XỬ LÝ GỬI MÃ OTP ---
    private void handleSendCode() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();

        // 1. Email/Username Không được để trống
        if (TextUtils.isEmpty(input)) {
            etUsernameOrEmail.setError("Không được để trống");
            etUsernameOrEmail.requestFocus();
            return;
        }

        // 2. Kiểm tra định dạng
        if (input.contains("@")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                etUsernameOrEmail.setError("Email không hợp lệ");
                etUsernameOrEmail.requestFocus();
                return;
            }
        } else {
            if (input.length() < 7) {
                etUsernameOrEmail.setError("Username phải >= 7 ký tự");
                etUsernameOrEmail.requestFocus();
                return;
            }
        }

        // Đổi trạng thái nút bấm
        btnSendCode.setEnabled(false);
        btnSendCode.setText("Đang gửi...");

        // GỌI API GỬI OTP
        SendOtpRequest request = new SendOtpRequest(input);
        authService.sendForgotPasswordOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("Gửi mã");

                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi mã xác nhận đến email của bạn", Toast.LENGTH_SHORT).show();
                    etCode.requestFocus(); // Tự động trỏ con trỏ chuột sang ô nhập mã
                } else {
                    String error = parseError(response);

                    // XỬ LÝ LOGIC TÀI KHOẢN GOOGLE Ở ĐÂY
                    if (error.contains("GOOGLE_ACCOUNT_NO_PASSWORD")) {
                        etUsernameOrEmail.setError("Tài khoản chưa có mật khẩu");
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Tài khoản đã đăng nhập bằng Google, vui lòng truy cập vào ứng dụng để thêm mật khẩu",
                                Toast.LENGTH_LONG).show();
                    } else if (error.contains("USER_NOT_FOUND")) {
                        etUsernameOrEmail.setError("Tài khoản không tồn tại");
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                    etUsernameOrEmail.requestFocus();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("Gửi mã");
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM XỬ LÝ XÁC NHẬN OTP ---
    private void handleConfirm() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();
        String code = Objects.requireNonNull(etCode.getText()).toString().trim();

        if (TextUtils.isEmpty(input)) {
            etUsernameOrEmail.setError("Không được để trống");
            etUsernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            etCode.setError("Không được để trống");
            etCode.requestFocus();
            return;
        }

        if (code.length() != 6) {
            etCode.setError("Mã phải gồm 6 chữ số");
            etCode.requestFocus();
            return;
        }

        // Đổi trạng thái nút bấm
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang kiểm tra...");

        // GỌI API XÁC NHẬN OTP
        VerifyOtpRequest request = new VerifyOtpRequest(input, code);
        authService.verifyOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");

                if (response.isSuccessful()) {
                    // MÃ ĐÚNG -> CHUYỂN MÀN HÌNH VÀ "GÓI" THEO EMAIL/USERNAME
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    // Dòng này cực kỳ quan trọng để màn hình sau biết đang đổi mật khẩu cho ai
                    intent.putExtra("EMAIL_OR_USERNAME", input);
                    startActivity(intent);
                    finish();
                } else {
                    String error = parseError(response);
                    if (error.contains("INVALID_OTP")) {
                        etCode.setError("Mã xác thực không chính xác");
                    } else if (error.contains("OTP_EXPIRED")) {
                        etCode.setError("Mã xác thực đã hết hạn, vui lòng gửi lại");
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                    etCode.requestFocus();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm phụ trợ để dịch lỗi từ Backend
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() == null) return "UNKNOWN_ERROR";
            return response.errorBody().string().trim();
        } catch (Exception e) {
            return "PARSE_ERROR";
        }
    }
}