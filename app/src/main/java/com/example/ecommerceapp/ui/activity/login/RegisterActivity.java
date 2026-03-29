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
import com.example.ecommerceapp.data.model.request.SendOtpRequest;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    private ApiService apiService;

    private TextInputEditText etCode;
    private MaterialButton btnSendCode;

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
        etCode = findViewById(R.id.etCode);
        btnSendCode = findViewById(R.id.btnSendCode);
    }

    // Thiết lập sự kiện
    private void initEvents() {

        // Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý đăng ký
        btnRegister.setOnClickListener(v -> handleRegister());

        btnSendCode.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

            // Kiểm tra định dạng email trước khi gửi
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Vui lòng nhập email hợp lệ trước khi gửi mã");
                etEmail.requestFocus();
                return;
            }

            // Hiển thị trạng thái đang gửi
            btnSendCode.setEnabled(false);
            btnSendCode.setText("Đang gửi...");

            SendOtpRequest request = new SendOtpRequest(email);
            apiService.sendRegisterOtp(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Mã OTP đã được gửi đến Email của bạn!", Toast.LENGTH_LONG).show();
                        btnSendCode.setText("Đã gửi");
                        // Thường ở đây người ta sẽ làm bộ đếm ngược 60s, nhưng tạm thời để "Đã gửi" cho đơn giản
                    } else {
                        btnSendCode.setEnabled(true);
                        btnSendCode.setText("Gửi mã");

                        // Đọc lỗi từ server (ví dụ: EMAIL_ALREADY_EXISTS)
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                            if (error.contains("EMAIL_ALREADY_EXISTS")) {
                                etEmail.setError("Email này đã được sử dụng");
                                etEmail.requestFocus();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Lỗi Server: " + error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText("Gửi mã");
                    Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Hàm xử lý đăng ký
    private void handleRegister() {
        etFullName.setError(null);
        etUsername.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        // BỔ SUNG 1: Xóa lỗi cũ của ô nhập mã OTP
        if (etCode != null) etCode.setError(null);

        String fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // BỔ SUNG 2: Lấy dữ liệu mã OTP người dùng nhập
        String otpCode = "";
        if (etCode != null && etCode.getText() != null) {
            otpCode = etCode.getText().toString().trim();
        }

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

        // BỔ SUNG 3: Bắt buộc phải nhập OTP và độ dài phải là 6
        if (TextUtils.isEmpty(otpCode)) {
            etCode.setError("Vui lòng nhập mã xác thực OTP");
            etCode.requestFocus();
            return;
        }

        if (otpCode.length() != 6) {
            etCode.setError("Mã OTP phải bao gồm 6 chữ số");
            etCode.requestFocus();
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

        // BỔ SUNG 4: Nhét mã OTP vào Request để gửi lên Server
        request.otpCode = otpCode;

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

            return response.errorBody().string().trim();

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

            case "INVALID_OTP":
                etCode.setError("Mã xác thực không chính xác");
                etCode.requestFocus();
                break;

            case "OTP_EXPIRED":
                etCode.setError("Mã xác thực đã hết hạn, vui lòng gửi lại");
                etCode.requestFocus();
                break;

            default:
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}