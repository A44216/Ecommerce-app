package com.example.ecommerceapp.ui.activity.home.admin.management;

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
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.SystemNotificationResponse;
import com.example.ecommerceapp.ui.adapter.admin.management.AdminNotificationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNotificationActivity extends AppCompatActivity {

    private ImageView ivBack, ivAddNotification;
    private RecyclerView rvNotifications;
    private TextView tvEmpty;

    private AdminNotificationAdapter adapter;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        tokenManager = TokenManager.getInstance(this);

        ivBack = findViewById(R.id.ivBack);
        ivAddNotification = findViewById(R.id.ivAddNotification);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmpty = findViewById(R.id.tvEmpty);

        ivBack.setOnClickListener(v -> finish());
        ivAddNotification.setOnClickListener(v -> {
            startActivity(new Intent(AdminNotificationActivity.this, AdminCreateNotificationActivity.class));
        });

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminNotificationAdapter();
        rvNotifications.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        ApiClient.getAdminNotificationService(tokenManager).getSystemNotifications().enqueue(new Callback<List<SystemNotificationResponse>>() {
            @Override
            public void onResponse(Call<List<SystemNotificationResponse>> call, Response<List<SystemNotificationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SystemNotificationResponse> list = response.body();
                    adapter.setData(list);

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(AdminNotificationActivity.this, "Không thể tải danh sách thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SystemNotificationResponse>> call, Throwable t) {
                Toast.makeText(AdminNotificationActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
