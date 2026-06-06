package com.example.ecommerceapp.ui.fragment.seller.notification;

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
import com.example.ecommerceapp.ui.activity.home.seller.order.SellerOrderDetailActivity;
import com.example.ecommerceapp.ui.adapter.user.OrderNotificationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerNotificationFragment extends Fragment {

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
                    
                    // Lọc chỉ lấy các thông báo dành cho Seller
                    List<NotificationResponse> sellerNotifs = new java.util.ArrayList<>();
                    for (NotificationResponse n : allNotifications) {
                        String type = n.getType();
                        if ("SELLER_ORDER".equals(type) || "PRODUCT".equals(type) || "SHOP".equals(type)) {
                            sellerNotifs.add(n);
                        }
                    }

                    if (sellerNotifs.isEmpty()) {
                        rvNotifications.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvNotifications.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        OrderNotificationAdapter adapter = new OrderNotificationAdapter(sellerNotifs, item -> {
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
                loadNotifications(); // Tải lại danh sách
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });

        // Điều hướng
        if (item.getRelatedId() != null && getActivity() != null) {
            if ("SELLER_ORDER".equals(item.getType())) {
                Intent intent = new Intent(getActivity(), SellerOrderDetailActivity.class);
                intent.putExtra("orderId", item.getRelatedId());
                startActivity(intent);
            } else if ("PRODUCT".equals(item.getType())) {
                Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity.class);
                intent.putExtra("productId", item.getRelatedId());
                startActivity(intent);
            }
        }
    }
}
