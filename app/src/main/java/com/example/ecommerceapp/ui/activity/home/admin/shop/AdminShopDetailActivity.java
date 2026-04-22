package com.example.ecommerceapp.ui.activity.home.admin.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.shop.AdminShopDetailResponse;
import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminShopDetailViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminShopDetailViewModelFactory;
import com.example.ecommerceapp.utils.NumberUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class AdminShopDetailActivity extends AppCompatActivity {

    private AdminShopDetailViewModel viewModel;

    private ImageView ivBack;
    private ProgressBar progressBar;
    private View contentLayout;

    private ShapeableImageView ivAvatar;
    private TextView tvShopName, tvStatus;
    private TextView tvEmail, tvPhone, tvAddress, tvDescription;
    private TextView tvRating, tvProducts, tvOrders, tvRevenue;

    private MaterialButton btnApprove, btnReject, btnBlock;

    private int shopId = -1;
    private boolean statusChanged = false;
    private ShopStatus currentStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_shop_detail);

        shopId = getIntent().getIntExtra("SHOP_ID", -1);
        if (shopId == -1) {
            Toast.makeText(this, "Lỗi ID cửa hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
        observeViewModel();

        viewModel.loadShopDetail(shopId);
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvShopName = findViewById(R.id.tvShopName);
        tvStatus = findViewById(R.id.tvStatus);

        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvDescription = findViewById(R.id.tvDescription);

        tvRating = findViewById(R.id.tvRating);
        tvProducts = findViewById(R.id.tvProducts);
        tvOrders = findViewById(R.id.tvOrders);
        tvRevenue = findViewById(R.id.tvRevenue);

        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        btnBlock = findViewById(R.id.btnBlock);
    }

    private void setupViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        AdminShopRepository repository = new AdminShopRepository(tokenManager);
        AdminShopDetailViewModelFactory factory = new AdminShopDetailViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(AdminShopDetailViewModel.class);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (statusChanged && currentStatus != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("STATUS_CHANGED", true);
                    resultIntent.putExtra("SHOP_ID", shopId);
                    resultIntent.putExtra("NEW_STATUS", currentStatus.name());
                    setResult(RESULT_OK, resultIntent);
                }
                finish();
            }
        });

        btnApprove.setOnClickListener(v -> {
            viewModel.updateShopStatus(shopId, ShopStatus.APPROVED);
        });

        btnReject.setOnClickListener(v -> {
            viewModel.updateShopStatus(shopId, ShopStatus.REJECTED);
        });

        btnBlock.setOnClickListener(v -> {
            viewModel.updateShopStatus(shopId, ShopStatus.BLOCKED);
        });
    }

    private void observeViewModel() {
        viewModel.getShopDetail().observe(this, detail -> {
            if (detail != null) {
                bindData(detail);
                currentStatus = detail.getStatus();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                statusChanged = true;
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(AdminShopDetailResponse detail) {
        tvShopName.setText(detail.getShopName() != null ? detail.getShopName() : "Không tên");
        tvEmail.setText("Email: " + (detail.getEmail() != null ? detail.getEmail() : "N/A"));
        tvPhone.setText("SĐT: " + (detail.getPhone() != null ? detail.getPhone() : "N/A"));
        tvAddress.setText("Địa chỉ: " + (detail.getAddress() != null ? detail.getAddress() : "N/A"));
        tvDescription.setText("Mô tả: " + (detail.getDescription() != null ? detail.getDescription() : "N/A"));

        tvRating.setText(String.format("Đánh giá: %.1f ⭐ (%d)",
                detail.getRatingAvg() != null ? detail.getRatingAvg().doubleValue() : 0.0,
                detail.getRatingCount() != null ? detail.getRatingCount() : 0));
        tvProducts.setText("Sản phẩm: " + (detail.getTotalProducts() != null ? detail.getTotalProducts() : 0));
        tvOrders.setText("Đơn hàng: " + (detail.getTotalOrders() != null ? detail.getTotalOrders() : 0));
        tvRevenue.setText("Doanh thu: " + NumberUtils.formatCompact(detail.getTotalRevenue() != null ? detail.getTotalRevenue() : java.math.BigDecimal.ZERO));

        com.example.ecommerceapp.utils.ImageLoader.load(this, ivAvatar, detail.getAvatar());

        bindStatus(detail.getStatus());
    }

    @SuppressLint("SetTextI18n")
    private void bindStatus(ShopStatus status) {
        if (status == null) {
            tvStatus.setVisibility(View.GONE);
            return;
        }
        tvStatus.setVisibility(View.VISIBLE);
        
        // Hide all buttons first, then show based on status
        btnApprove.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        btnBlock.setVisibility(View.GONE);

        switch (status) {
            case APPROVED:
                tvStatus.setText("Đã duyệt");
                tvStatus.setBackgroundResource(R.drawable.bg_shop_status_approved);
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
                btnBlock.setVisibility(View.VISIBLE); // Only option is to block
                break;
            case PENDING:
                tvStatus.setText("Chờ duyệt");
                tvStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
                break;
            case REJECTED:
                tvStatus.setText("Bị từ chối");
                tvStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                btnApprove.setVisibility(View.VISIBLE); // Can reconsider
                break;
            case BLOCKED:
                tvStatus.setText("Bị khóa");
                tvStatus.setBackgroundResource(R.drawable.bg_shop_status_blocked);
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                btnApprove.setVisibility(View.VISIBLE); // Allow unblocking by approving
                break;
        }
    }
}
