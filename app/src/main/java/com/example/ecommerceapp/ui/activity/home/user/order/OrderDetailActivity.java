package com.example.ecommerceapp.ui.activity.home.user.order;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;
import com.example.ecommerceapp.ui.adapter.user.UserOrderItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvAddress, tvTotal;
    private RecyclerView rvItems;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // 1. Ánh xạ
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvTotal = findViewById(R.id.tvTotalPayment);
        rvItems = findViewById(R.id.rvOrderDetailItems);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 2. Lấy ID từ Intent
        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderDetail();
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrderDetail() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        ApiClient.getUserOrderApiService(tokenManager).getOrderById(orderId).enqueue(new Callback<UserOrderResponse>() {
            @Override
            public void onResponse(Call<UserOrderResponse> call, Response<UserOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserOrderResponse order = response.body();

                    // Hiển thị dữ liệu
                    tvStatus.setText(order.getStatus().toString());
                    tvTotal.setText(String.format("%,.0fđ", order.getTotalPrice()));

                    if (order.getAddressLine() != null) {
                        // Nối chuỗi để hiển thị: "Tên Khách Hàng - 0987654321 \n Số nhà, Đường..."
                        String fullAddress = order.getShippingName() + " - " + order.getShippingPhone() + "\n" + order.getAddressLine();
                        tvAddress.setText(fullAddress);
                    } else {
                        tvAddress.setText("Chưa cập nhật địa chỉ");
                    }

                    // Setup danh sách sản phẩm trong đơn
                    rvItems.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));

                    // --- ĐÃ BỎ COMMENT ĐOẠN NÀY ĐỂ HIỂN THỊ SẢN PHẨM MUA ---
                    if (order.getOrderItems() != null) {
                        rvItems.setAdapter(new UserOrderItemAdapter(order.getOrderItems()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserOrderResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}