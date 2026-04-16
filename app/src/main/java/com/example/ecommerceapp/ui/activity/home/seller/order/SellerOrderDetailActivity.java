package com.example.ecommerceapp.ui.activity.home.seller.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderDetailAdapter;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderStatusAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SellerOrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId;
    private TextView tvCreatedAt;
    private TextView tvCustomer;
    private TextView tvShippingName, tvPhone, tvAddress, tvPaymentMethod, tvPaymentStatus, tvTotalPrice;

    private RecyclerView rvOrder, rvStatus;
    private ImageView ivBack;

    private MaterialButton btnCancel, btnConfirm;

    private SellerOrderDetailAdapter adapter;
    private SellerOrderViewModel viewModel;
    private SellerOrderStatusAdapter statusAdapter;

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

        rvOrder = findViewById(R.id.rvOrderItems);
        rvStatus = findViewById(R.id.rvOrderStatus);

        ivBack = findViewById(R.id.ivBack);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvShippingName = findViewById(R.id.tvShippingName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

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
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                this,
                new SellerOrderViewModelFactory(tokenManager)
        ).get(SellerOrderViewModel.class);
    }

    private void initListeners() {

        ivBack.setOnClickListener(v -> finish());

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, SellerProductDetailActivity.class);
            intent.putExtra("productId", item.getProductId());
            startActivity(intent);
        });

        statusAdapter.setOnStatusChangeListener(status -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
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

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Đổi sang: " + getStatusText(selectedStatus) + "?")
                    .setPositiveButton("OK", (dialog, which) -> {

                        viewModel.updateOrderStatus(orderId, selectedStatus);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
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

                    tvOrderId.setText("Đơn #" + data.getOrderId());

                    String createdAt = data.getCreatedAt();
                    if (createdAt != null && createdAt.contains("T")) {
                        createdAt = createdAt.split("T")[0];
                    }
                    tvCreatedAt.setText("Ngày: " + createdAt);

                    tvCustomer.setText("Khách hàng: " + data.getCustomerName());
                    tvShippingName.setText("Người nhận: " + data.getShippingName());
                    tvPhone.setText("Sđt: " + data.getShippingPhone());
                    tvAddress.setText("Địa chỉ: " + data.getShippingAddress());

                    tvPaymentMethod.setText(String.valueOf(data.getPaymentMethod().getLabel()));
                    tvPaymentStatus.setText(String.valueOf(data.getPaymentStatus().getLabel()));

                    if (data.getTotalPrice() != null) {
                        tvTotalPrice.setText(String.format("%,.0f", data.getTotalPrice()) + " đ");
                    }

                    adapter.setData(data.getItems());

                    OrderStatus currentStatus =
                            data.getStatus() != null ? data.getStatus() : OrderStatus.PENDING;

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