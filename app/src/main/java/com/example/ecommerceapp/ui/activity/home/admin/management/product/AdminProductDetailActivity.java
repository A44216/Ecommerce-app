package com.example.ecommerceapp.ui.activity.home.admin.management.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminProductService;
import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminProductDetailResponse;
import com.example.ecommerceapp.ui.adapter.admin.product.AdminImagePagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductDetailActivity extends AppCompatActivity {

    private TextView txtName, txtPrice, txtShopName, txtCategory, txtStock, txtSold, txtRating, txtStatus, txtCreatedAt, txtDescription;
    private ViewPager2 viewPagerImages;
    private ImageView ivBack;
    private WormDotsIndicator dotsIndicator;
    
    private MaterialButton btnApprove, btnReject, btnDelete, btnRestore;

    private AdminProductService service;
    private int productId;
    private boolean isDeletedTab = false;
    private boolean isDataChanged = false;
    private String lastAction = null;
    private ProductStatus lastNewStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initListeners();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isDataChanged) {
                    Intent intent = new Intent();
                    intent.putExtra("productId", productId);
                    if (lastAction != null) {
                        intent.putExtra("action", lastAction);
                    }
                    if (lastNewStatus != null) {
                        intent.putExtra("newStatus", lastNewStatus.name());
                    }
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });

        service = ApiClient.getAdminProductService(TokenManager.getInstance(this));

        productId = getIntent().getIntExtra("productId", -1);
        isDeletedTab = getIntent().getBooleanExtra("isDeletedTab", false);

        if (productId != -1) {
            fetchProductDetail(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtShopName = findViewById(R.id.txtShopName);
        txtCategory = findViewById(R.id.txtCategory);
        txtStock = findViewById(R.id.txtStock);
        txtSold = findViewById(R.id.txtSold);
        txtRating = findViewById(R.id.txtRating);
        txtStatus = findViewById(R.id.txtStatus);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtDescription = findViewById(R.id.txtDescription);
        
        viewPagerImages = findViewById(R.id.viewPagerImages);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        ivBack = findViewById(R.id.ivBack);
        
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        btnDelete = findViewById(R.id.btnDelete);
        btnRestore = findViewById(R.id.btnRestore);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        
        btnApprove.setOnClickListener(v -> showConfirmDialog(
                "Xác nhận duyệt",
                "Bạn có chắc chắn muốn duyệt sản phẩm này không?",
                () -> updateProductStatus(ProductStatus.APPROVED)
        ));
        
        btnReject.setOnClickListener(v -> showConfirmDialog(
                "Xác nhận từ chối",
                "Bạn có chắc chắn muốn từ chối sản phẩm này không?",
                () -> updateProductStatus(ProductStatus.REJECTED)
        ));
        
        btnDelete.setOnClickListener(v -> showConfirmDialog(
                "Xác nhận xóa",
                "Bạn có chắc chắn muốn xóa sản phẩm này không?",
                () -> deleteProduct()
        ));
        
        btnRestore.setOnClickListener(v -> showConfirmDialog(
                "Xác nhận khôi phục",
                "Bạn có chắc chắn muốn khôi phục sản phẩm này không?",
                () -> restoreProduct()
        ));
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Hủy", null)
                .show();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(AdminProductDetailResponse product) {
        txtName.setText(product.getName());
        txtPrice.setText("Giá: " + String.format("%,.0f", product.getPrice()) + " đ");
        txtShopName.setText("Shop: " + (product.getShopName() != null ? product.getShopName() : "N/A"));
        txtCategory.setText("Danh mục: " + (product.getCategoryName() != null ? product.getCategoryName() : "Khác"));
        txtStock.setText("Kho: " + product.getStock());
        txtSold.setText("Đã bán: " + product.getSoldCount());
        
        if (product.getRatingAvg() != null) {
            txtRating.setText("Đánh giá: " + product.getRatingAvg().setScale(1, RoundingMode.HALF_UP) + " ⭐ (" + product.getRatingCount() + " đánh giá)");
        } else {
            txtRating.setText("Đánh giá: 0.0 ⭐ (0 đánh giá)");
        }
        
        boolean isDeleted = isDeletedTab;
        
        if (isDeleted) {
            txtStatus.setText("Trạng thái: Đã xóa");
        } else {
            txtStatus.setText("Trạng thái: " + product.getStatus().getLabel());
        }
        
        if (product.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            txtCreatedAt.setText("Ngày tạo: " + product.getCreatedAt().format(formatter));
        } else {
            txtCreatedAt.setText("Ngày tạo: N/A");
        }

        txtDescription.setText(product.getDescription() != null
                ? "Mô tả:\n" + product.getDescription()
                : "Chưa có mô tả");

        // Load ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            AdminImagePagerAdapter adapter = new AdminImagePagerAdapter(this, product.getImages());
            viewPagerImages.setAdapter(adapter);

            dotsIndicator.setVisibility(View.VISIBLE);
            dotsIndicator.attachTo(viewPagerImages);
        } else {
            viewPagerImages.setAdapter(null);
            dotsIndicator.setVisibility(View.GONE);
        }
        
        // Show/Hide buttons based on status
        btnApprove.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnRestore.setVisibility(View.GONE);

        if (isDeleted) {
            btnRestore.setVisibility(View.VISIBLE);
        } else {
            if (product.getStatus() == ProductStatus.PENDING) {
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            } else if (product.getStatus() == ProductStatus.APPROVED) {
                btnDelete.setVisibility(View.VISIBLE);
            } else if (product.getStatus() == ProductStatus.REJECTED) {
                // No buttons are shown for REJECTED
            }
        }
    }

    private void fetchProductDetail(int id) {
        service.getProductById(id).enqueue(new Callback<AdminProductDetailResponse>() {
            @Override
            public void onResponse(Call<AdminProductDetailResponse> call, Response<AdminProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminProductDetailResponse> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductStatus(ProductStatus status) {
        service.updateProductStatus(productId, status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    lastAction = "UPDATE_STATUS";
                    lastNewStatus = status;
                    setResultAndFetch();
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProduct() {
        service.deleteProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductDetailActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    isDeletedTab = true;
                    lastAction = "DELETE";
                    setResultAndFetch();
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void restoreProduct() {
        service.restoreProduct(productId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductDetailActivity.this, "Đã khôi phục sản phẩm", Toast.LENGTH_SHORT).show();
                    isDeletedTab = false;
                    lastAction = "RESTORE";
                    setResultAndFetch();
                } else {
                    Toast.makeText(AdminProductDetailActivity.this, "Khôi phục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResultAndFetch() {
        isDataChanged = true;
        fetchProductDetail(productId);
    }
}
