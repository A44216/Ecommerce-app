package com.example.ecommerceapp.ui.activity.home.user.order;

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

public class UserOrderHistoryActivity extends AppCompatActivity {

    private UserOrderViewModel viewModel;
    private UserOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_history);

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

        // 5. GỌI API (Vẫn dùng ID 15 của bạn để test nhé)
        int dummyUserId = 15;
        viewModel.fetchOrdersByUser(dummyUserId);
    }
}