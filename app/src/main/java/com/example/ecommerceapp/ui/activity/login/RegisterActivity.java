package com.example.ecommerceapp.ui.activity.login;

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
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.model.request.UserRequest;
import com.example.ecommerceapp.data.model.response.UserResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = ApiClient.getApiService(); // Khởi tạo apiService

        initViews();
        initEvents();

    }

    // Ánh xạ view
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
    }

    // Thiết lập sự kiện
    private void initEvents() {

        // Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    // Hàm xử lý đăng ký
    private void handleRegister() {
        String fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // Validate

        // 1. Không được để trống
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Không được để trống");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Không được để trống");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Không được để trống");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Không được để trống");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Không được để trống");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Không được để trống");
            etConfirmPassword.requestFocus();
            return;
        }

        // 2. Kiểm tra username
        if (username.length() < 7) {
            etUsername.setError("Username phải >= 7 ký tự");
            etUsername.requestFocus();
            return;
        }

        // 3. Kiểm tra email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        // 4. Kiểm tra số điện thoại
        if (!phone.matches("^0\\d{9}$")) {
            etPhone.setError("SĐT phải 10 số và bắt đầu bằng số 0");
            etPhone.requestFocus();
            return;
        }

        // 5. Kiểm tra mật khẩu
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải >= 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        // 6. Kiểm tra xác nhận mật khẩu
        if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // Tạo request
        UserRequest request = new UserRequest();
        request.fullName = fullName;
        request.username = username;
        request.email = email;
        request.phone = phone;
        request.password = password;
        request.role = Role.CUSTOMER;

        // Gọi API
        apiService.createUser(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // đóng activity
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}