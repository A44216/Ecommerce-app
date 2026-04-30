package com.example.ecommerceapp.ui.activity.home.admin.profile;

import android.annotation.SuppressLint;
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
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.profile.AdminChangePasswordRequest;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import java.util.Map;

import retrofit2.Call;

public class AdminChangePasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnFinish;

    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_change_password);
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

        initView();
        setupListener();

    }

    private void initView() {
        ivBack = findViewById(R.id.ivBack);
        btnFinish = findViewById(R.id.btnFinish);

        layoutCurrentPassword = findViewById(R.id.layoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
    }

    private void setupListener() {

        // nút back
        ivBack.setOnClickListener(v -> finish());

        // nút đổi mật khẩu
        btnFinish.setOnClickListener(v -> validateAndSubmit());

        etCurrentPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutCurrentPassword.setError(null);
            }

            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etNewPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutNewPassword.setError(null);
            }

            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etConfirmPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutConfirmPassword.setError(null);
            }

            @Override public void afterTextChanged(android.text.Editable s) {}
        });

    }

    private void validateAndSubmit() {

        // reset lỗi
        layoutCurrentPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);

        String current = etCurrentPassword.getText() != null
                ? etCurrentPassword.getText().toString().trim() : "";

        String newPass = etNewPassword.getText() != null
                ? etNewPassword.getText().toString().trim() : "";

        String confirm = etConfirmPassword.getText() != null
                ? etConfirmPassword.getText().toString().trim() : "";

        // validate rỗng
        if (TextUtils.isEmpty(current)) {
            layoutCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            layoutNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPass.length() < 6) {
            layoutNewPassword.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }

        if (newPass.equals(current)) {
            layoutNewPassword.setError("Mật khẩu mới không được trùng mật khẩu cũ");
            return;
        }

        if (TextUtils.isEmpty(confirm)) {
            layoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            return;
        }

        if (!newPass.equals(confirm)) {
            layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn đổi mật khẩu không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> submitChangePassword(current, newPass))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void submitChangePassword(String current, String newPass) {

        // disable button + loading text
        btnFinish.setEnabled(false);
        btnFinish.setText("Đang xử lý...");

        TokenManager tokenManager = TokenManager.getInstance(this);
        AuthService authService = ApiClient.getAuthService(tokenManager);

        AdminChangePasswordRequest request =
                new AdminChangePasswordRequest(current, newPass);

        authService.changePassword(request).enqueue(new retrofit2.Callback<Map<String, String>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Map<String, String>> call, retrofit2.Response<Map<String, String>> response) {

                // enable lại button
                btnFinish.setEnabled(true);
                btnFinish.setText("Hoàn thành");

                if (response.isSuccessful() && response.body() != null) {

                    String msg = response.body().get("message");

                    // map message backend -> UI
                    if ("PASSWORD_CHANGED_SUCCESSFULLY".equals(msg)) {
                        msg = "Đổi mật khẩu thành công";
                    }

                    new AlertDialog.Builder(AdminChangePasswordActivity.this)
                            .setTitle("Thành công")
                            .setMessage(msg + ". Vui lòng đăng nhập lại.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                tokenManager.logout();
                                Intent intent = new Intent(AdminChangePasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .show();

                } else {

                    if (response.code() == 400) {

                        layoutCurrentPassword.setError("Mật khẩu hiện tại không đúng");

                    } else if (response.code() == 401) {

                        Toast.makeText(AdminChangePasswordActivity.this,
                                "Phiên đăng nhập hết hạn",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(AdminChangePasswordActivity.this,
                                "Lỗi hệ thống",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {

                // enable lại button
                btnFinish.setEnabled(true);
                btnFinish.setText("Hoàn thành");

                Toast.makeText(AdminChangePasswordActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}