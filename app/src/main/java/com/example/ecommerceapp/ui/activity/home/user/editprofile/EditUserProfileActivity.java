package com.example.ecommerceapp.ui.activity.home.user.editprofile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.UserUpdateRequest;
import com.example.ecommerceapp.data.model.request.UserUpdateRequest;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etPhone, etEmail;
    private Button btnSaveProfile;
    private ImageView btnBack;

    private UserService userService;
    private TokenManager tokenManager;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        // 1. Ánh xạ View
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 2. Cài đặt API
        tokenManager = TokenManager.getInstance(this);
        currentUserId = tokenManager.getUserId();
        userService = ApiClient.getUserService(tokenManager);

        // 3. Tải dữ liệu cũ điền vào ô
        loadCurrentProfile();

        // 4. Sự kiện bấm Lưu
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentProfile() {
        if (currentUserId == -1) return;

        userService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();
                    etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
                    etUsername.setText(user.getUsername() != null ? user.getUsername() : "");
                    etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                    etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(EditUserProfileActivity.this, "Lỗi tải thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Tên và Username không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserUpdateRequest request = new UserUpdateRequest(name, username, email, phone);

        userService.updateUserProfile(currentUserId, request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Cập nhật xong thì tắt màn hình này về lại tab Hồ sơ
                } else {
                    Toast.makeText(EditUserProfileActivity.this, "Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(EditUserProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}