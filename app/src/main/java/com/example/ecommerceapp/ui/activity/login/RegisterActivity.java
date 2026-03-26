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

        apiService = ApiClient.getPublicApiService();

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
        etFullName.setError(null);
        etUsername.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

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

        if (!TextUtils.isEmpty(phone) && !phone.matches("^0\\d{9}$")) {
            etPhone.setError("SĐT phải 10 số và bắt đầu bằng số 0");
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
        if (!username.matches("^[a-zA-Z0-9_]{7,}$")) {
            etUsername.setError("Username chỉ gồm chữ, số và _");
            etUsername.requestFocus();
            return;
        }

        // 3. Kiểm tra email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            return;
        }

        // 4. Kiểm tra mật khẩu
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải >= 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        // 5. Kiểm tra xác nhận mật khẩu
        if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        username = username.toLowerCase();
        email = email.toLowerCase();

        // Tạo request
        UserRequest request = new UserRequest();
        request.fullName = fullName;
        request.username = username;
        request.email = email;
        request.phone = TextUtils.isEmpty(phone) ? null : phone;
        request.password = password;
        request.role = Role.CUSTOMER;

        // Gọi API
        apiService.register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String error = parseError(response);
                showFieldError(error);
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() == null) return "UNKNOWN_ERROR";

            String errorBody = response.errorBody().string();

            if (errorBody.trim().startsWith("{")) {
                org.json.JSONObject json = new org.json.JSONObject(errorBody);

                if (json.has("message")) return json.getString("message");
                if (json.has("error")) return json.getString("error");
            }

            return errorBody;

        } catch (Exception e) {
            return "PARSE_ERROR";
        }
    }

    private void showFieldError(String error) {

        etUsername.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);

        if (error == null) {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (error) {

            case "USERNAME_EXIST":
                etUsername.setError("Username đã tồn tại");
                etUsername.requestFocus();
                break;

            case "EMAIL_EXIST":
                etEmail.setError("Email đã tồn tại");
                etEmail.requestFocus();
                break;

            case "PHONE_EXIST":
                etPhone.setError("Số điện thoại đã tồn tại");
                etPhone.requestFocus();
                break;

            case "INVALID_INPUT":
                Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                break;

            case "INVALID_USERNAME":
                etUsername.setError("Username không hợp lệ (7+ ký tự, không ký tự đặc biệt)");
                etUsername.requestFocus();
                break;

            case "INVALID_EMAIL":
                etEmail.setError("Email không hợp lệ");
                etEmail.requestFocus();
                break;

            case "INVALID_PASSWORD":
                etPassword.setError("Mật khẩu phải >= 6 ký tự");
                etPassword.requestFocus();
                break;

            case "INVALID_PHONE":
                etPhone.setError("Số điện thoại không hợp lệ");
                etPhone.requestFocus();
                break;

            default:
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}