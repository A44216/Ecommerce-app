package com.example.ecommerceapp.ui.activity.home.admin.profile;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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

        if (TextUtils.isEmpty(confirm)) {
            layoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            return;
        }

        if (!newPass.equals(confirm)) {
            layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        submitChangePassword(current, newPass);
    }

    private void submitChangePassword(String current, String newPass) {

        Toast.makeText(this,
                "Đổi mật khẩu thành công",
                Toast.LENGTH_SHORT).show();

        finish();
    }

}