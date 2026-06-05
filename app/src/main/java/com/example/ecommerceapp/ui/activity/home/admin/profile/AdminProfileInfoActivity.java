package com.example.ecommerceapp.ui.activity.home.admin.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.AdminProfileRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminProfileViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminProfileViewModelFactory;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.ImageUploadHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminProfileInfoActivity extends AppCompatActivity {

    private AdminProfileViewModel viewModel;

    private TextInputEditText etFullName, edtPhone, edtEmail;
    private ImageView ivBack, imgAvatar;
    private MaterialButton btnChooseImage;
    private ScrollView svProfileInfo;
    private ProgressBar progressBarProfileInfo, progressBarAvatar;

    private String avatarUrl = "";
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() != RESULT_OK || result.getData() == null) return;

                        Uri uri = result.getData().getData();
                        if (uri == null) return;

                        selectedImageUri = uri;
                        ImageLoader.load(AdminProfileInfoActivity.this, imgAvatar, uri);
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_profile_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding = getResources().getDimensionPixelSize(R.dimen.screen_padding_small);
            v.setPadding(
                    padding + systemBars.left,
                    padding + systemBars.top,
                    padding + systemBars.right,
                    padding + systemBars.bottom
            );
            return insets;
        });

        initView();
        setupListeners();
        initViewModel();
        observeData();
        viewModel.loadProfile();

    }

    private void initView() {
        etFullName = findViewById(R.id.etFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        imgAvatar = findViewById(R.id.imgAvatar);
        ivBack = findViewById(R.id.ivBack);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        
        svProfileInfo = findViewById(R.id.svProfileInfo);
        progressBarProfileInfo = findViewById(R.id.progressBarProfileInfo);
        progressBarAvatar = findViewById(R.id.progressBarAvatar);
    }

    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        findViewById(R.id.btnUpdateProfileInfo).setOnClickListener(v -> {

            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
            String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";

            if (fullName.isEmpty()) {
                etFullName.setError("Không được để trống tên");
                return;
            }

            if (email.isEmpty()) {
                edtEmail.setError("Không được để trống email");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                return;
            }

            if (!phone.isEmpty() && !phone.matches("^0\\d{9}$")) {
                edtPhone.setError("SĐT phải 10 số và bắt đầu bằng số 0");
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận cập nhật")
                    .setMessage("Bạn có chắc muốn cập nhật profile không?")
                    .setPositiveButton("Cập nhật", (dialog, which) -> {
                        if (selectedImageUri != null) {
                            if (progressBarProfileInfo != null) progressBarProfileInfo.setVisibility(View.VISIBLE);
                            if (svProfileInfo != null) svProfileInfo.setVisibility(View.GONE);

                            ImageUploadHelper.uploadImage(
                                    TokenManager.getInstance(AdminProfileInfoActivity.this),
                                    selectedImageUri,
                                    AdminProfileInfoActivity.this,
                                    new ImageUploadHelper.Callback<String>() {
                                        @Override
                                        public void onSuccess(String url) {
                                            avatarUrl = url;
                                            viewModel.updateProfile(fullName, email, phone, avatarUrl);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            if (progressBarProfileInfo != null) progressBarProfileInfo.setVisibility(View.GONE);
                                            if (svProfileInfo != null) svProfileInfo.setVisibility(View.VISIBLE);
                                            Toast.makeText(AdminProfileInfoActivity.this, "Lỗi upload ảnh: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        } else {
                            viewModel.updateProfile(fullName, email, phone, avatarUrl);
                        }
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void initViewModel() {

        TokenManager tm = TokenManager.getInstance(this);
        AdminProfileRepository repo = new AdminProfileRepository(tm);

        AdminProfileViewModelFactory factory = new AdminProfileViewModelFactory(repo);

        viewModel = new ViewModelProvider(this, factory)
                .get(AdminProfileViewModel.class);
    }

    private void observeData() {
        viewModel.getLoading().observe(this, isLoading -> {
            if (progressBarProfileInfo != null) progressBarProfileInfo.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (svProfileInfo != null) svProfileInfo.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getProfileData().observe(this, data -> {

            if (data == null) return;

            etFullName.setText(data.getFullName());
            edtPhone.setText(data.getPhone());
            edtEmail.setText(data.getEmail());

            avatarUrl = data.getAvatar();

            ImageLoader.load(this, imgAvatar, avatarUrl);
        });

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                viewModel.loadProfile();
                finish();
            }
        });

        viewModel.getError().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );
    }

}