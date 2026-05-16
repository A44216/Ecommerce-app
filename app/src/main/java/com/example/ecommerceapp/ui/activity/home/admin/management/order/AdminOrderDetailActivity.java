package com.example.ecommerceapp.ui.activity.home.admin.management.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderDetailResponse;
import com.example.ecommerceapp.data.repository.admin.AdminOrderRepository;
import com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminProductDetailActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.shop.AdminShopDetailActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.user.AdminUserDetailActivity;
import com.example.ecommerceapp.ui.adapter.admin.order.AdminOrderItemAdapter;
import com.example.ecommerceapp.utils.TimeUtils;

import android.content.Intent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderCode, tvOrderStatus, tvCreatedAt, tvCompletedAt;
    private TextView tvShopName;
    private TextView tvCustomer, tvShippingName, tvPhone, tvAddress;
    private TextView tvSubtotal, tvDiscount, tvTotalPrice, tvPlatformFee, tvSellerRevenue;
    private TextView tvPaymentMethod, tvPaymentStatus;
    private RecyclerView rvOrderItems;
    private AdminOrderItemAdapter adapter;
    private AdminOrderRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        initViews();
        loadData();
    }

    private void initViews() {
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvCompletedAt = findViewById(R.id.tvCompletedAt);

        tvShopName = findViewById(R.id.tvShopName);

        tvCustomer = findViewById(R.id.tvCustomer);
        tvShippingName = findViewById(R.id.tvShippingName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPlatformFee = findViewById(R.id.tvPlatformFee);
        tvSellerRevenue = findViewById(R.id.tvSellerRevenue);

        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);

        rvOrderItems = findViewById(R.id.rvOrderItems);
        adapter = new AdminOrderItemAdapter();
        adapter.setListener(item -> {
            if (item.getProductId() != null) {
                Intent intent = new Intent(AdminOrderDetailActivity.this, AdminProductDetailActivity.class);
                intent.putExtra("productId", item.getProductId());
                startActivity(intent);
            }
        });
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(adapter);

        repository = new AdminOrderRepository(TokenManager.getInstance(this));
    }

    private void loadData() {
        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) return;

        repository.getOrderById(orderId).enqueue(new Callback<AdminOrderDetailResponse>() {
            @Override
            public void onResponse(Call<AdminOrderDetailResponse> call, Response<AdminOrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(AdminOrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminOrderDetailResponse> call, Throwable t) {
                Toast.makeText(AdminOrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(AdminOrderDetailResponse data) {
        tvOrderCode.setText("Mã ĐH: " + (data.getOrderCode() != null ? data.getOrderCode() : "N/A"));
        tvOrderStatus.setText("Trạng thái: " + (data.getStatus() != null ? data.getStatus().getLabel() : "N/A"));
        
        if (data.getCreatedAt() != null) {
            tvCreatedAt.setText("Ngày tạo: " + TimeUtils.formatDateTime(data.getCreatedAt().toString()));
        } else {
            tvCreatedAt.setText("Ngày tạo: --");
        }
        
        if (data.getCompletedAt() != null) {
            tvCompletedAt.setText("Hoàn thành: " + TimeUtils.formatDateTime(data.getCompletedAt().toString()));
            tvCompletedAt.setVisibility(android.view.View.VISIBLE);
        } else {
            tvCompletedAt.setVisibility(android.view.View.GONE);
        }

        tvShopName.setText(data.getShopName() != null ? data.getShopName() : "--");
        tvShopName.setOnClickListener(v -> {
            if (data.getShopId() != null) {
                Intent intent = new Intent(AdminOrderDetailActivity.this, AdminShopDetailActivity.class);
                intent.putExtra("shopId", data.getShopId());
                startActivity(intent);
            }
        });

        tvCustomer.setText(data.getFullName() != null ? data.getFullName() : "--");
        tvCustomer.setOnClickListener(v -> {
            if (data.getUserId() != null) {
                Intent intent = new Intent(AdminOrderDetailActivity.this, AdminUserDetailActivity.class);
                intent.putExtra("userId", data.getUserId());
                startActivity(intent);
            }
        });
        tvShippingName.setText("Người nhận: " + (data.getShippingName() != null ? data.getShippingName() : "--"));
        tvPhone.setText("SĐT: " + (data.getShippingPhone() != null ? data.getShippingPhone() : "--"));
        tvAddress.setText("Địa chỉ: " + (data.getShippingAddress() != null ? data.getShippingAddress() : "--"));

        if (data.getSubtotal() != null) tvSubtotal.setText(String.format("%,.0f đ", data.getSubtotal()));
        if (data.getDiscountAmount() != null) tvDiscount.setText(String.format("- %,.0f đ", data.getDiscountAmount()));
        if (data.getTotalPrice() != null) tvTotalPrice.setText(String.format("%,.0f đ", data.getTotalPrice()));
        if (data.getPlatformFeeAmount() != null) {
            String rate = data.getPlatformFeeRate() != null ? " (" + data.getPlatformFeeRate().stripTrailingZeros().toPlainString() + "%)" : "";
            TextView txtPlatformFee = findViewById(R.id.txtPlatformFee);
            txtPlatformFee.setText("Phí hệ thống" + rate + ":");
            tvPlatformFee.setText(String.format("- %,.0f đ", data.getPlatformFeeAmount()));
        }
        if (data.getSellerReceived() != null) tvSellerRevenue.setText(String.format("%,.0f đ", data.getSellerReceived()));

        tvPaymentMethod.setText(data.getPaymentMethod() != null ? data.getPaymentMethod().getLabel() : "--");
        tvPaymentStatus.setText(data.getPaymentStatus() != null ? data.getPaymentStatus().getLabel() : "--");

        if (data.getItems() != null) {
            adapter.setData(data.getItems());
        }

        com.google.android.material.button.MaterialButton btnRefund = findViewById(R.id.btnRefund);
        com.google.android.material.button.MaterialButton btnCompleted = findViewById(R.id.btnCompleted);

        if (data.getStatus() == com.example.ecommerceapp.data.enums.OrderStatus.DISPUTED) {
            btnRefund.setVisibility(android.view.View.VISIBLE);
            btnCompleted.setVisibility(android.view.View.VISIBLE);

            btnRefund.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Phán quyết")
                        .setMessage("Đồng ý hoàn tiền cho Khách hàng?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            resolveDispute(data.getId(), "REFUND");
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            });

            btnCompleted.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Phán quyết")
                        .setMessage("Từ chối hoàn tiền, tiền sẽ được chuyển cho Shop?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            resolveDispute(data.getId(), "COMPLETED");
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            });
        } else {
            btnRefund.setVisibility(android.view.View.GONE);
            btnCompleted.setVisibility(android.view.View.GONE);
        }
    }

    private void resolveDispute(int orderId, String decision) {
        repository.resolveDispute(orderId, decision).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminOrderDetailActivity.this, "Đã đưa ra phán quyết thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AdminOrderDetailActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminOrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
