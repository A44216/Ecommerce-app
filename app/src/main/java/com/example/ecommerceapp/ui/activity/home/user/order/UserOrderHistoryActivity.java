package com.example.ecommerceapp.ui.activity.home.user.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.UserOrderRepository;
import com.example.ecommerceapp.ui.adapter.user.UserOrderAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserOrderViewModelFactory;
// Nếu có Activity chi tiết, hãy import nó vào đây, ví dụ:
// import com.example.ecommerceapp.ui.activity.home.user.order.OrderDetailActivity;

public class UserOrderHistoryActivity extends AppCompatActivity {

    private UserOrderViewModel viewModel;
    private UserOrderAdapter adapter;

    private String currentStatus = "ALL";
    private int specificOrderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_history);

        // --- NHẬN TÍN HIỆU TỪ TRANG KHÁC (PROFILE HOẶC THÔNG BÁO) ---
        if (getIntent() != null) {
            // Trường hợp 1: Từ Profile truyền qua Trạng thái
            if (getIntent().hasExtra("ORDER_STATUS")) {
                currentStatus = getIntent().getStringExtra("ORDER_STATUS");
                Toast.makeText(this, "Đang lọc: " + currentStatus, Toast.LENGTH_SHORT).show();
            }

            // Trường hợp 2: Từ Thông báo truyền qua ID Đơn hàng
            if (getIntent().hasExtra("ORDER_ID")) {
                specificOrderId = getIntent().getIntExtra("ORDER_ID", -1);
            }
        }

        // 1. Ánh xạ View
        ImageView btnBack = findViewById(R.id.btnOrderHistoryBack);
        RecyclerView rvOrderHistory = findViewById(R.id.rvOrderHistory);

        btnBack.setOnClickListener(v -> finish());

        // 2. Cài đặt RecyclerView
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserOrderAdapter();
        rvOrderHistory.setAdapter(adapter);

        // 3. Khởi tạo MVVM
        TokenManager tokenManager = TokenManager.getInstance(this);
        UserOrderApiService apiService = ApiClient.getUserOrderApiService(tokenManager);
        UserOrderRepository repository = new UserOrderRepository(apiService);
        UserOrderViewModelFactory factory = new UserOrderViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(UserOrderViewModel.class);

        // 4. Lắng nghe dữ liệu
        viewModel.getOrderList().observe(this, userOrderResponses -> {
            if (userOrderResponses != null && !userOrderResponses.isEmpty()) {
                adapter.updateData(userOrderResponses);
            } else {
                Toast.makeText(this, "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(this, s ->
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
        );

        // ==========================================
        // 5. GỌI API THEO LUỒNG ĐIỀU HƯỚNG
        // ==========================================
        int realUserId = (int) tokenManager.getUserId();

        if (realUserId != -1) {
            // Nếu có specificOrderId, tức là đi từ Thông báo sang -> Chuyển hướng hoặc xử lý đặc biệt
            if (specificOrderId != -1) {
                Toast.makeText(this, "Đang mở đơn hàng: " + specificOrderId, Toast.LENGTH_SHORT).show();
                viewModel.fetchOrdersByUserAndStatus(realUserId, currentStatus);

            } else {
                // Nếu không có specificOrderId, tức là đi từ Profile sang -> Lọc danh sách bình thường
                viewModel.fetchOrdersByUserAndStatus(realUserId, currentStatus);
            }
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }
}