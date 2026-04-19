package com.example.ecommerceapp.ui.activity.home.user.editprofile;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.UserUpdateRequest;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.example.ecommerceapp.utils.ImageLoader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etPhone, etEmail;
    private TextView tvChangeEmail;
    private Button btnSaveProfile;
    private ImageView btnBack, ivAvatarEdit;

    private UserService userService;
    private TokenManager tokenManager;
    private long currentUserId;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        ImageLoader.load(this, ivAvatarEdit, imageUri);
                        uploadAvatarToServer(imageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        ivAvatarEdit = findViewById(R.id.ivAvatarEdit);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        tvChangeEmail = findViewById(R.id.tvChangeEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);

        tokenManager = TokenManager.getInstance(this);
        currentUserId = tokenManager.getUserId();
        userService = ApiClient.getUserService(tokenManager);

        // KHÓA USERNAME NGAY TỪ ĐẦU
        etUsername.setEnabled(false);
        etUsername.setKeyListener(null);
        etUsername.setTextColor(Color.GRAY);

        btnBack.setOnClickListener(v -> finish());
        ivAvatarEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentProfile();
    }

    private void loadCurrentProfile() {
        if (currentUserId == -1) return;
        userService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse user = response.body();
                    etFullName.setText(user.getFullName());
                    etUsername.setText(user.getUsername());
                    etPhone.setText(user.getPhone());
                    etEmail.setText(user.getEmail());

                    // QUAN TRỌNG: KIỂM TRA CỜ GOOGLE ACCOUNT TỪ SERVER
                    if (user.isGoogleAccount()) {
                        // Khóa ô Email tương tự Username
                        etEmail.setHint("Email Google (Cố định)");
                        tvChangeEmail.setVisibility(android.view.View.GONE);
                    } else {
                        tvChangeEmail.setVisibility(android.view.View.VISIBLE);
                        tvChangeEmail.setOnClickListener(v -> {
                            Intent intent = new Intent(EditUserProfileActivity.this, ChangeEmailActivity.class);
                            intent.putExtra("CURRENT_EMAIL", user.getEmail());
                            startActivity(intent);
                        });
                    }
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
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserUpdateRequest request = new UserUpdateRequest(name, null, email, phone);

        userService.updateUserProfile(currentUserId, request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditUserProfileActivity.this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(EditUserProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Các hàm phụ trợ ảnh giữ nguyên...
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "temp_avatar.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, length);
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) { return null; }
    }

    private void uploadAvatarToServer(Uri imageUri) {
        File file = getFileFromUri(imageUri);
        if (file == null) return;
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        userService.uploadAvatar(currentUserId, body).enqueue(new retrofit2.Callback<UserProfileResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserProfileResponse> call, retrofit2.Response<UserProfileResponse> response) {
                if (response.isSuccessful()) Toast.makeText(EditUserProfileActivity.this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(retrofit2.Call<UserProfileResponse> call, Throwable t) {}
        });
    }
}