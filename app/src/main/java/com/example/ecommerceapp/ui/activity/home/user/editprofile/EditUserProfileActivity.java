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
    private Button btnSaveProfile;
    private ImageView btnBack;

    private UserService userService;
    private TokenManager tokenManager;
    private long currentUserId;
    private ImageView ivAvatarEdit; // Khai báo biến giao diện

    // Trình lắng nghe kết quả khi người dùng chọn ảnh xong
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // 1. DÙNG IMAGELOADER ĐỂ HIỂN THỊ ẢNH NGAY LẬP TỨC
                        ImageLoader.load(this, ivAvatarEdit, imageUri);

                        // 2. Gửi ảnh lên server
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

        // Khi bấm vào hình avatar thì mở thư viện ảnh
        ivAvatarEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });
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

    // Hàm phụ trợ 1: Chuyển Uri của Android thành 1 File vật lý để gửi mạng
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "temp_avatar.jpg"); // Lưu vào cache
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hàm phụ trợ 2: Gói File lại và Bắn lên Server
    private void uploadAvatarToServer(Uri imageUri) {
        File file = getFileFromUri(imageUri);
        if (file == null) {
            Toast.makeText(this, "Không thể đọc file ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Multipart Body chuẩn bị gửi
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        // Gọi API của bạn (Nhớ đổi tên userService nếu bạn đặt khác)
        userService.uploadAvatar(currentUserId, body).enqueue(new retrofit2.Callback<UserProfileResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UserProfileResponse> call, retrofit2.Response<UserProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditUserProfileActivity.this, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditUserProfileActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(EditUserProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}