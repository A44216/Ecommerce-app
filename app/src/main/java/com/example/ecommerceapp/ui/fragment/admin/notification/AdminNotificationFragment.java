package com.example.ecommerceapp.ui.fragment.admin.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.NotificationResponse;
import com.example.ecommerceapp.ui.activity.home.admin.management.order.AdminOrderDetailActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.shop.AdminShopDetailActivity;
import com.example.ecommerceapp.ui.adapter.user.OrderNotificationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminNotificationFragment extends Fragment {

    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TokenManager tokenManager;
    private UserService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_role_notification, container, false);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        tokenManager = TokenManager.getInstance(getContext());
        apiService = ApiClient.getUserService(tokenManager);

        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        long userId = tokenManager.getUserId();
        if (userId == -1) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        swipeRefreshLayout.setRefreshing(true);
        apiService.getMyNotifications(userId).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                if (isAdded()) swipeRefreshLayout.setRefreshing(false);
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<NotificationResponse> allNotifications = response.body();

                    if (allNotifications.isEmpty()) {
                        rvNotifications.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvNotifications.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        OrderNotificationAdapter adapter = new OrderNotificationAdapter(allNotifications, item -> {
                            handleNotificationClick(item);
                        });
                        rvNotifications.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                if (isAdded()) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleNotificationClick(NotificationResponse item) {
        // Gọi API đánh dấu đã đọc
        apiService.markNotificationAsRead(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadNotifications(); // Tải lại danh sách để xóa màu "chưa đọc"
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });

        // Điều hướng
        if (item.getType() != null && item.getRelatedId() != null && getActivity() != null) {
            String type = item.getType();
            Integer relatedId = item.getRelatedId();

            if ("SHOP".equals(type)) {
                Intent intent = new Intent(getActivity(), AdminShopDetailActivity.class);
                intent.putExtra("shopId", relatedId);
                startActivity(intent);
            } else if ("ORDER".equals(type)) {
                Intent intent = new Intent(getActivity(), AdminOrderDetailActivity.class);
                intent.putExtra("orderId", relatedId);
                startActivity(intent);
            } else if ("SYSTEM".equals(type)) {
                String title = item.getTitle() != null ? item.getTitle().toLowerCase() : "";
                if (title.contains("trả hàng") || title.contains("tranh chấp") || title.contains("phân xử")) {
                    Intent intent = new Intent(getActivity(), AdminOrderDetailActivity.class);
                    intent.putExtra("orderId", relatedId);
                    startActivity(intent);
                } else if (title.contains("gian hàng")) {
                    Intent intent = new Intent(getActivity(), AdminShopDetailActivity.class);
                    intent.putExtra("SHOP_ID", relatedId);
                    startActivity(intent);
                } else if (title.contains("sản phẩm")) {
                    Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminProductDetailActivity.class);
                    intent.putExtra("productId", relatedId);
                    startActivity(intent);
                }
            }
        }
    }
}
