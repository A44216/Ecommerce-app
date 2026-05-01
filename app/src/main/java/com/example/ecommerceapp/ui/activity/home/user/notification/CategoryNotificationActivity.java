package com.example.ecommerceapp.ui.activity.home.user.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.NotificationResponse;
import com.example.ecommerceapp.ui.adapter.user.OrderNotificationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNotificationActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle, tvEmpty;
    private RecyclerView rvNotifications;

    private String notificationType;
    private String notificationTitle;
    private TokenManager tokenManager;
    private UserService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_notification);

        notificationType = getIntent().getStringExtra("NOTIFICATION_TYPE");
        notificationTitle = getIntent().getStringExtra("NOTIFICATION_TITLE");

        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvNotifications = findViewById(R.id.rvNotifications);

        if (notificationTitle != null) {
            tvTitle.setText(notificationTitle);
        }

        ivBack.setOnClickListener(v -> finish());

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        tokenManager = TokenManager.getInstance(this);
        apiService = ApiClient.getUserService(tokenManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        long userId = tokenManager.getUserId();
        if (userId == -1 || notificationType == null) return;

        apiService.getMyNotificationsByType(userId, notificationType).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationResponse> list = response.body();

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);

                        OrderNotificationAdapter adapter = new OrderNotificationAdapter(list, item -> {
                            handleNotificationClick(item);
                        });
                        rvNotifications.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(CategoryNotificationActivity.this, "Không thể tải danh sách thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                Toast.makeText(CategoryNotificationActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNotificationClick(NotificationResponse item) {
        apiService.markNotificationAsRead(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadNotifications();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });

        if (item.getType() != null) {
            if ("PROMOTION".equals(item.getType())) {
                Toast.makeText(this, "Mở trang Khuyến mãi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thông báo: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
