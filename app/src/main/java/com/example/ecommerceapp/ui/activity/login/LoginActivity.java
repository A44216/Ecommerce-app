package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.request.UserRequest;
import com.example.ecommerceapp.data.model.response.UserResponse;
import com.example.ecommerceapp.ui.activity.home.AdminHomeActivity;
import com.example.ecommerceapp.ui.activity.home.SellerHomeActivity;
import com.example.ecommerceapp.ui.activity.home.UserHomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvRegister, tvForgotPassword;
    private TextInputEditText etUsernameOrEmail, etPassword;
    private MaterialButton btnLogin;
    private CheckBox chkRememberLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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

        apiService = ApiClient.getApiService(); // Khởi tạo apiService

        initViews();
        initEvents();

        }

    // Ánh xạ view
    private void initViews() {
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        chkRememberLogin = findViewById(R.id.chkRememberLogin);
    }

    // Thiết lập sự kiện
    private void initEvents() {

        // Chuyển sang màn hình Register
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Chuyển sang màn hình ForgotPassword
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý login
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();

        // Validate

        // 1. Email/Username và Password Không được để trống
        if (TextUtils.isEmpty(input)) {
            etUsernameOrEmail.setError("Không được để trống");
            etUsernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Không được để trống");
            etPassword.requestFocus();
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
            // 3. Kiểm tra Username
            if (input.length() < 7) {
                etUsernameOrEmail.setError("Username phải >= 7 ký tự");
                etUsernameOrEmail.requestFocus();
                return;
            }
        }

        // 4. Kiểm tra Password
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải >= 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        // Tạo request
        UserRequest request = new UserRequest();
        if (input.contains("@")) {
            request.email = input;
        } else {
            request.username = input;
        }
        request.password = password;

        // Gọi API login
        apiService.loginUser(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body(); // Lấy dữ liệu user từ API
                    if (user.role != null) {
                        switch (user.role) {
                            case ADMIN:
                                startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                break;
                            case SELLER:
                            case CUSTOMER:
                                startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                                break;
                        }
                        finish(); // đóng LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công nhưng không xác định role", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}