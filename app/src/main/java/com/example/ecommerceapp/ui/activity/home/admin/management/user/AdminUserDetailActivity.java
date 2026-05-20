package com.example.ecommerceapp.ui.activity.home.admin.management.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserShopInfoResponse;
import com.example.ecommerceapp.data.repository.admin.AdminUserRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminUserDetailViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminUserDetailViewModelFactory;
import com.example.ecommerceapp.utils.ImageLoader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.format.DateTimeFormatter;

public class AdminUserDetailActivity extends AppCompatActivity {

    private AdminUserDetailViewModel viewModel;
    private int userId;

    private ImageView ivBack;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    
    // User Info
    private ShapeableImageView ivAvatar;
    private TextView tvFullName, tvUsername, tvRole, tvStatus, tvEmail, tvPhone, tvCreatedAt;
    
    // Shop Info
    private LinearLayout llShopInfoContainer;
    private TextView tvShopName, tvShopDescription, tvShopRating, tvShopOrders;
    
    private MaterialButton btnChangeStatus;
    private AdminUserDetailResponse currentUserDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupObservers();

        viewModel.getUserDetail(userId);
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        
        ivAvatar = findViewById(R.id.ivAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvUsername = findViewById(R.id.tvUsername);
        tvRole = findViewById(R.id.tvRole);
        tvStatus = findViewById(R.id.tvStatus);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        
        llShopInfoContainer = findViewById(R.id.llShopInfoContainer);
        tvShopName = findViewById(R.id.tvShopName);
        tvShopDescription = findViewById(R.id.tvShopDescription);
        tvShopRating = findViewById(R.id.tvShopRating);
        tvShopOrders = findViewById(R.id.tvShopOrders);
        
        btnChangeStatus = findViewById(R.id.btnChangeStatus);

        ivBack.setOnClickListener(v -> finish());
        
        btnChangeStatus.setOnClickListener(v -> showConfirmDialog());
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        AdminUserRepository repository = new AdminUserRepository(tokenManager);
        viewModel = new ViewModelProvider(this, new AdminUserDetailViewModelFactory(repository))
                .get(AdminUserDetailViewModel.class);
    }

    private void setupObservers() {
        viewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUserDetailLiveData().observe(this, detail -> {
            if (detail != null) {
                currentUserDetail = detail;
                populateData(detail);
            }
        });

        viewModel.getStatusChangeSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Thay đổi trạng thái thành công", Toast.LENGTH_SHORT).show();
                // Data will be reloaded by ViewModel, and finish() will handle returning the latest status
            }
        });
    }

    private void populateData(AdminUserDetailResponse detail) {
        scrollView.setVisibility(View.VISIBLE);
        btnChangeStatus.setVisibility(View.VISIBLE);

        tvFullName.setText(detail.getFullName() != null ? detail.getFullName() : "Chưa cập nhật");
        tvUsername.setText(detail.getUsername() != null ? "@" + detail.getUsername() : "");
        tvEmail.setText(detail.getEmail() != null ? detail.getEmail() : "Trống");
        tvPhone.setText(detail.getPhone() != null ? detail.getPhone() : "Trống");

        if (detail.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tvCreatedAt.setText(detail.getCreatedAt().format(formatter));
        } else {
            tvCreatedAt.setText("Trống");
        }

        // Role
        if (detail.getRole() == Role.SELLER) {
            tvRole.setText("Người bán");
            tvRole.setBackgroundResource(R.drawable.bg_role_seller);
            
            // Show shop info
            llShopInfoContainer.setVisibility(View.VISIBLE);
            AdminUserShopInfoResponse shop = detail.getShop();
            if (shop != null) {
                tvShopName.setText(shop.getShopName() != null ? shop.getShopName() : "Chưa cập nhật");
                tvShopName.setOnClickListener(v -> {
                    if (shop.getId() != null) {
                        android.content.Intent intent = new android.content.Intent(AdminUserDetailActivity.this, com.example.ecommerceapp.ui.activity.home.admin.management.shop.AdminShopDetailActivity.class);
                        intent.putExtra("shopId", shop.getId());
                        startActivity(intent);
                    }
                });
                tvShopDescription.setText(shop.getDescription() != null ? shop.getDescription() : "Không có mô tả");
                tvShopRating.setText(shop.getRatingAvg() != null ? shop.getRatingAvg() + " ★" : "0 ★");
                tvShopOrders.setText(shop.getTotalOrders() != null ? String.valueOf(shop.getTotalOrders()) : "0");
            }
        } else {
            tvRole.setText("Khách hàng");
            tvRole.setBackgroundResource(R.drawable.bg_role_customer);
            llShopInfoContainer.setVisibility(View.GONE);
        }

        // Status
        if (detail.getStatus() == UserStatus.ACTIVE) {
            tvStatus.setText("Hoạt động");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            
            btnChangeStatus.setText("VÔ HIỆU HÓA TÀI KHOẢN");
            btnChangeStatus.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            tvStatus.setText("Vô hiệu hóa");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvStatus.setBackgroundResource(R.drawable.bg_status_blocked);
            
            btnChangeStatus.setText("MỞ KHÓA TÀI KHOẢN");
            btnChangeStatus.setBackgroundColor(getResources().getColor(R.color.green));
        }

        ImageLoader.load(this, ivAvatar, detail.getAvatar());
    }

    private void showConfirmDialog() {
        if (currentUserDetail == null) return;
        
        boolean isActive = currentUserDetail.getStatus() == UserStatus.ACTIVE;
        String actionStr = isActive ? "vô hiệu hóa" : "mở khóa";
        UserStatus newStatus = isActive ? UserStatus.BLOCKED : UserStatus.ACTIVE;

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn " + actionStr + " tài khoản này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    viewModel.changeStatus(userId, newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void finish() {
        if (currentUserDetail != null) {
            android.content.Intent resultIntent = new android.content.Intent();
            resultIntent.putExtra("userId", userId);
            resultIntent.putExtra("newStatus", currentUserDetail.getStatus().name());
            setResult(RESULT_OK, resultIntent);
        }
        super.finish();
    }
}
