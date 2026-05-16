package com.example.ecommerceapp.ui.activity.home.seller.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerOrderRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderDetailAdapter;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderStatusAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SellerOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderCode, tvCreatedAt, tvCompletedAt;
    private TextView tvCustomer, tvShippingName, tvPhone, tvAddress;
    private TextView tvSubtotal, txtDiscount, tvDiscount, txtPlatformFee, tvPlatformFee, tvSellerRevenue;
    private TextView tvPaymentMethod, tvPaymentStatus;

    private RecyclerView rvOrder, rvStatus;
    private ImageView ivBack;

    private MaterialButton btnCancel, btnConfirm;

    private SellerOrderDetailAdapter adapter;
    private SellerOrderViewModel viewModel;
    private SellerOrderStatusAdapter statusAdapter;
    private android.widget.ScrollView svOrderDetail;
    private android.widget.ProgressBar progressBarOrderDetail;

    private TokenManager tokenManager;

    private int orderId;
    private OrderStatus selectedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_detail);

        tokenManager = TokenManager.getInstance(this);

        initViews();
        initViewModel();
        initListeners();
        observeData();

        observeUpdateStatus();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        rvOrder = findViewById(R.id.rvOrderItems);
        rvStatus = findViewById(R.id.rvOrderStatus);

        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvCompletedAt = findViewById(R.id.tvCompletedAt);

        tvCustomer = findViewById(R.id.tvCustomer);
        tvShippingName = findViewById(R.id.tvShippingName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        txtPlatformFee = findViewById(R.id.txtPlatformFee);
        tvPlatformFee = findViewById(R.id.tvPlatformFee);
        tvSellerRevenue = findViewById(R.id.tvSellerRevenue);

        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        adapter = new SellerOrderDetailAdapter();
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(adapter);

        statusAdapter = new SellerOrderStatusAdapter();
        rvStatus.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        rvStatus.setAdapter(statusAdapter);

        orderId = getIntent().getIntExtra("orderId", 0);
        svOrderDetail = findViewById(R.id.svOrderDetail);
        progressBarOrderDetail = findViewById(R.id.progressBarOrderDetail);
    }

    private void initViewModel() {

        SellerOrderRepository repository = new SellerOrderRepository(tokenManager);
        viewModel = new ViewModelProvider(this, new SellerOrderViewModelFactory(repository)).get(SellerOrderViewModel.class);
    }

    private void initListeners() {

        ivBack.setOnClickListener(v -> finish());

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, SellerProductDetailActivity.class);
            intent.putExtra("productId", item.getProductId());
            startActivity(intent);
        });

        statusAdapter.setOnStatusChangeListener(status -> {

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận thay đổi")
                    .setMessage("Đổi sang: " + getStatusText(status) + "?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        selectedStatus = status;
                        statusAdapter.setSelectedStatus(status);
                    })
                    .setNegativeButton("Huỷ", (d, w) -> {
                        statusAdapter.setSelectedStatus(selectedStatus);
                    })
                    .show();
        });

        btnConfirm.setOnClickListener(v -> {

            if (selectedStatus == null) return;

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Đổi sang: " + getStatusText(selectedStatus) + "?")
                    .setPositiveButton("OK", (dialog, which) -> {

                        viewModel.updateOrderStatus(orderId, selectedStatus);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Huỷ đơn hàng")
                    .setMessage("Bạn chắc chắn muốn huỷ đơn này?")
                    .setPositiveButton("Huỷ đơn", (dialog, which) -> {

                        viewModel.updateOrderStatus(orderId, OrderStatus.CANCELED);
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void observeData() {

        viewModel.getOrderDetail(orderId)
                .observe(this, data -> {

                    if (data == null) return;

                    if (progressBarOrderDetail != null) progressBarOrderDetail.setVisibility(View.GONE);
                    if (svOrderDetail != null) svOrderDetail.setVisibility(View.VISIBLE);

                    // OrderId
                    tvOrderCode.setText("Mã đơn: " + data.getOrderCode());

                    // CreatedAt
                    String createdAt = data.getCreatedAt();
                    if (createdAt != null && createdAt.contains("T")) {
                        createdAt = createdAt.split("T")[0];
                    }
                    tvCreatedAt.setText("Ngày tạo: " + createdAt);

                    // CompletedAt
                    String completedAt = data.getCompletedAt();
                    if (completedAt != null) {
                        if (completedAt.contains("T")) {
                            completedAt = completedAt.split("T")[0];
                        }
                        tvCompletedAt.setText("Hoàn tất: " + completedAt);
                        tvCompletedAt.setVisibility(View.VISIBLE);
                    } else {
                        tvCompletedAt.setVisibility(View.GONE);
                    }

                    // Customer info
                    tvCustomer.setText("Khách hàng: " + data.getCustomerName());
                    tvShippingName.setText("Người nhận: " + data.getShippingName());
                    tvPhone.setText("Sđt: " + data.getShippingPhone());
                    tvAddress.setText("Địa chỉ: " + data.getShippingAddress());

                    // Finance
                    if (data.getSubtotal() != null) {
                        tvSubtotal.setText(String.format("%,.0f", data.getSubtotal()) + " đ");
                    }

                    if (data.getPlatformFeeRate() != null && data.getPlatformFeeAmount() != null) {

                        tvPlatformFee.setText(String.format("- %,.0f đ", data.getPlatformFeeAmount()));

                        txtPlatformFee.setText("Phí nền tảng (" + data.getPlatformFeeRate().stripTrailingZeros().toPlainString() + "%):");
                    }

                    if (data.getSellerRevenue() != null) {
                        tvSellerRevenue.setText(String.format("%,.0f", data.getSellerRevenue()) + " đ");
                    }

                    // Payment method
                    tvPaymentMethod.setText(String.valueOf(data.getPaymentMethod().getLabel()));
                    tvPaymentStatus.setText(String.valueOf(data.getPaymentStatus().getLabel()));

                    // Order Status
                    adapter.setData(data.getItems());
                    OrderStatus currentStatus = data.getStatus() != null ? data.getStatus() : OrderStatus.PENDING;
                    selectedStatus = currentStatus;

                    // CANCELED (FINAL - RED)
                    if (currentStatus == OrderStatus.CANCELED) {

                        statusAdapter.setData(
                                java.util.Collections.singletonList(OrderStatus.CANCELED),
                                OrderStatus.CANCELED
                        );

                        btnConfirm.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);

                        return;
                    }

                    // COMPLETED (FINAL - GREEN)
                    if (currentStatus == OrderStatus.COMPLETED) {

                        statusAdapter.setData(
                                java.util.Collections.singletonList(OrderStatus.COMPLETED),
                                OrderStatus.COMPLETED
                        );

                        btnConfirm.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);

                        return;
                    }

                    // RETURN_REQUESTED (DISPUTE WORKFLOW)
                    if (currentStatus == OrderStatus.RETURN_REQUESTED) {
                        statusAdapter.setData(
                                java.util.Collections.singletonList(OrderStatus.RETURN_REQUESTED),
                                OrderStatus.RETURN_REQUESTED
                        );

                        btnConfirm.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);

                        btnConfirm.setText("Đồng ý hoàn tiền");
                        btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(this, R.color.green)));
                        btnConfirm.setOnClickListener(v -> {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Đồng ý hoàn tiền")
                                    .setMessage("Bạn chắc chắn muốn đồng ý hoàn tiền cho đơn hàng này?")
                                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                                        viewModel.acceptReturn(orderId);
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        });

                        btnCancel.setText("Từ chối");
                        btnCancel.setBackgroundTintList(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(this, R.color.red)));
                        btnCancel.setOnClickListener(v -> {
                            android.widget.EditText input = new android.widget.EditText(this);
                            input.setHint("Nhập lý do từ chối...");
                            
                            android.widget.FrameLayout container = new android.widget.FrameLayout(this);
                            android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            params.leftMargin = 50;
                            params.rightMargin = 50;
                            input.setLayoutParams(params);
                            container.addView(input);

                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Từ chối hoàn tiền (Khiếu nại Admin)")
                                    .setMessage("Vui lòng nhập lý do từ chối yêu cầu trả hàng của khách:")
                                    .setView(container)
                                    .setPositiveButton("Gửi khiếu nại", (dialog, which) -> {
                                        String reason = input.getText().toString();
                                        if (reason.trim().isEmpty()) {
                                            android.widget.Toast.makeText(this, "Vui lòng nhập lý do!", android.widget.Toast.LENGTH_SHORT).show();
                                        } else {
                                            viewModel.rejectReturn(orderId, reason);
                                        }
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        });
                        return;
                    }
                    
                    // DISPUTED, RETURNED handling
                    if (currentStatus == OrderStatus.DISPUTED || currentStatus == OrderStatus.RETURNED) {
                        statusAdapter.setData(
                                java.util.Collections.singletonList(currentStatus),
                                currentStatus
                        );
                        btnConfirm.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        return;
                    }

                    // NORMAL FLOW
                    statusAdapter.setData(getStatusFlow(), currentStatus);

                    btnConfirm.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);

                    // reset button UI
                    btnConfirm.setText("Xác nhận");
                    btnConfirm.setEnabled(true);
                    btnConfirm.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    androidx.core.content.ContextCompat.getColor(
                                            this,
                                            R.color.green
                                    )
                            )
                    );
                });
    }

    private List<OrderStatus> getStatusFlow() {
        return java.util.Arrays.asList(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPING,
                OrderStatus.COMPLETED
        );
    }

    private String getStatusText(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Chờ xác nhận";
            case CONFIRMED:
                return "Đã xác nhận";
            case SHIPPING:
                return "Đang giao hàng";
            case COMPLETED:
                return "Hoàn thành";
            default:
                return status.name();
        }
    }

    private void observeUpdateStatus() {

        viewModel.getUpdateStatusResult().observe(this, success -> {

            if (success != null && success) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}