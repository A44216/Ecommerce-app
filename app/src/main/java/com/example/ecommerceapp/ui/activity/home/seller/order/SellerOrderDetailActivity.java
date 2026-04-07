package com.example.ecommerceapp.ui.activity.home.seller.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    MaterialButton btnCancel, btnConfirm;
    private SellerOrderDetailAdapter adapter;
    private SellerOrderViewModel viewModel;
    private SellerOrderStatusAdapter statusAdapter;

    private TokenManager tokenManager;
    private int orderId;
    private int shopId;
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
    }

    private void initViews() {
        rvOrder = findViewById(R.id.rvOrderItems);
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

        adapter = new SellerOrderDetailAdapter();
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(adapter);

        orderId = getIntent().getIntExtra("orderId", 0);
        shopId = (int) tokenManager.getShopId();

        rvStatus = findViewById(R.id.rvOrderStatus);

        statusAdapter = new SellerOrderStatusAdapter();
        rvStatus.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        rvStatus.setAdapter(statusAdapter);

        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
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

                        // CHỈ SET khi user confirm
                        selectedStatus = status;

                        statusAdapter.setSelectedStatus(status);
                    })
                    .setNegativeButton("Huỷ", (d, w) -> {
                        // rollback UI về trạng thái cũ
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

                        viewModel.updateOrderStatus(orderId, shopId, selectedStatus);

                        finish();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Huỷ đơn hàng")
                    .setMessage("Bạn chắc chắn muốn huỷ đơn này?")
                    .setPositiveButton("Huỷ đơn", (dialog, which) -> {

                        viewModel.updateOrderStatus(orderId, shopId, OrderStatus.CANCELED);

                        finish(); // thêm dòng này
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void observeData() {
        viewModel.getOrderDetail(orderId, shopId)
                .observe(this, data -> {
                    if (data == null) return;

                    // ORDER INFO
                    tvOrderId.setText("Đơn #" + data.getOrderId());

                    String createdAt = data.getCreatedAt();
                    if (createdAt != null && createdAt.contains("T")) {
                        createdAt = createdAt.split("T")[0];
                    }
                    tvCreatedAt.setText("Ngày: " + createdAt);

                    // CUSTOMER + SHIPPING
                    tvCustomer.setText("Khách hàng: " + data.getCustomerName());
                    tvShippingName.setText("Người nhận: " + data.getShippingName());
                    tvPhone.setText("Sdt: " + data.getShippingPhone());
                    tvAddress.setText("Địa chỉ: " + data.getShippingAddress());

                    // PAYMENT
                    tvPaymentMethod.setText(String.valueOf(data.getPaymentMethod()));
                    tvPaymentStatus.setText(String.valueOf(data.getPaymentStatus()));

                    // TOTAL
                    if (data.getTotalPrice() != null) {
                        tvTotalPrice.setText(String.format("%,.0f", data.getTotalPrice()) + " đ");
                    }

                    // ITEMS
                    adapter.setData(data.getItems());

                    OrderStatus currentStatus =
                            data.getStatus() != null ? data.getStatus() : OrderStatus.PENDING;

                    selectedStatus = currentStatus;

                    statusAdapter.setData(
                            getStatusFlow(),
                            currentStatus
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


}