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
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.response.LoginResponse;
import com.example.ecommerceapp.ui.activity.home.AdminHomeActivity;
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
    private TokenManager tokenManager;

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

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getPublicApiService();

        if (tokenManager.isRememberLogin() && tokenManager.getToken() != null && tokenManager.getRole() != null) {
//            tokenManager.clearToken();
            checkRememberLogin();
        }

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

    private void checkRememberLogin() {
        String role = tokenManager.getRole();

        if ("ADMIN".equals(role)) {
            startActivity(new Intent(this, AdminHomeActivity.class));
        } else {
            startActivity(new Intent(this, UserHomeActivity.class));
        }

        finish();
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString();

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
        LoginRequest request = new LoginRequest();
        if (input.contains("@")) {
            request.email = input;
        } else {
            request.username = input;
        }
        request.password = password;

        // Gọi API login
        apiService.loginUser(request).enqueue(new Callback<LoginResponse>() {            @Override
        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

            if (response.isSuccessful() && response.body() != null) {

                LoginResponse user = response.body();

                // Save token
                tokenManager.saveToken(user.token);
                // Save role
                tokenManager.saveRole(user.role);
                // Lưu remember
                tokenManager.setRememberLogin(chkRememberLogin.isChecked());
                // Save userId
                tokenManager.saveUserId(user.id);

                Toast.makeText(LoginActivity.this,
                        "Login success: " + user.role,
                        Toast.LENGTH_SHORT).show();

                switch (user.role) {
                    case "ADMIN":
                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                        break;

                    case "SELLER":
                    case "CUSTOMER":
                        startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                        break;
                    default:
                        Toast.makeText(LoginActivity.this,
                                "Role không hợp lệ: " + user.role,
                                Toast.LENGTH_SHORT).show();

                        tokenManager.clearToken();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                        break;
                }

                finish();

            } else {

                String error = parseError(response);

                showFieldError(error);
            }
        }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() == null) return "UNKNOWN_ERROR";
            return response.errorBody().string().trim();
        } catch (Exception e) {
            return "PARSE_ERROR";
        }
    }

    private void showFieldError(String error) {

        etUsernameOrEmail.setError(null);
        etPassword.setError(null);

        switch (error) {

            case "WRONG_PASSWORD":
                etPassword.setError("Sai mật khẩu");
                etPassword.requestFocus();
                etPassword.setSelection(etPassword.length());
                break;

            case "USER_NOT_FOUND":
                etUsernameOrEmail.setError("Tài khoản không tồn tại");
                etUsernameOrEmail.requestFocus();
                break;

            case "ACCOUNT_BLOCKED":
                etUsernameOrEmail.setError("Tài khoản đã bị khóa");
                etUsernameOrEmail.requestFocus();
                break;

            case "INVALID_INPUT":
                etUsernameOrEmail.setError("Dữ liệu không hợp lệ");
                etUsernameOrEmail.requestFocus();
                break;

            default:
                etUsernameOrEmail.setError("Lỗi: " + error);
                etUsernameOrEmail.requestFocus();
                break;
        }
    }

}