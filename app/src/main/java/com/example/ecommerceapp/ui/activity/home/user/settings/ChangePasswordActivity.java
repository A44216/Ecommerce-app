package com.example.ecommerceapp.ui.activity.home.user.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ChangePasswordRequest;
import com.example.ecommerceapp.data.model.request.SendOtpRequest;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextView tvEmailInfo;
    private Button btnSendOtp, btnSavePassword;
    private EditText etOtpCode, etNewPassword;

    private String currentUserEmail = "";
    private long currentUserId;
    private UserService userService;
    private AuthService authService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        tvEmailInfo = findViewById(R.id.tvEmailInfo);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        etOtpCode = findViewById(R.id.etOtpCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        ImageView ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());

        tokenManager = TokenManager.getInstance(this);
        currentUserId = tokenManager.getUserId();
        userService = ApiClient.getUserService(tokenManager);
        authService = ApiClient.getAuthService(); // Dùng AuthService công khai để gửi OTP

        // 1. Vừa vào màn hình là lấy Email ngay
        loadUserEmail();

        // 2. Nút Gửi OTP (Chỉ bấm được khi đã lấy được Email)
        btnSendOtp.setOnClickListener(v -> sendOtpToEmail());

        // 3. Nút Lưu Mật khẩu mới
        btnSavePassword.setOnClickListener(v -> changePassword());
    }

    private void loadUserEmail() {
        userService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserEmail = response.body().getEmail();
                    if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
                        // Làm mờ bớt email đi cho chuyên nghiệp (Tùy chọn)
                        tvEmailInfo.setText("Mã OTP sẽ được gửi đến: " + currentUserEmail);
                        btnSendOtp.setEnabled(true);
                    } else {
                        tvEmailInfo.setText("Tài khoản của bạn chưa có Email. Không thể đổi mật khẩu.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                tvEmailInfo.setText("Lỗi kết nối, không lấy được Email.");
            }
        });
    }

    private void sendOtpToEmail() {
        btnSendOtp.setEnabled(false);
        btnSendOtp.setText("Đang gửi...");

        // Tận dụng lại chính API Gửi mã quên mật khẩu!
        SendOtpRequest request = new SendOtpRequest(currentUserEmail);
        authService.sendForgotPasswordOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSendOtp.setText("Gửi lại mã");
                btnSendOtp.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đã gửi mã xác nhận đến Email!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi gửi mã OTP", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSendOtp.setText("Lỗi mạng");
            }
        });
    }

    private void changePassword() {
        String otp = etOtpCode.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();

        if (otp.length() != 6) {
            etOtpCode.setError("Mã OTP phải 6 số"); return;
        }
        if (newPass.length() < 6) {
            etNewPassword.setError("Mật khẩu phải >= 6 ký tự"); return;
        }

        btnSavePassword.setEnabled(false);

        // Gọi API Bảo mật mới tạo
        ChangePasswordRequest request = new ChangePasswordRequest(otp, newPass);
        userService.changePassword(currentUserId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // Xóa token và bắt đăng nhập lại để đảm bảo bảo mật
                    tokenManager.clearAllData();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    btnSavePassword.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi: Mã OTP không chính xác hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSavePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}