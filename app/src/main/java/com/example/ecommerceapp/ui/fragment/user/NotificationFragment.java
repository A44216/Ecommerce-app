package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.NotificationResponse;
import com.example.ecommerceapp.data.model.ui.NotificationItem;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.OrderDetailActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.ui.adapter.user.NotificationAdapter;
import com.example.ecommerceapp.ui.adapter.user.OrderNotificationAdapter;
import com.example.ecommerceapp.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private TextView tvCartBadge;
    private RecyclerView rvOrderNotifications;
    private LinearLayout layoutEmptyOrder;
    private TokenManager tokenManager;
    private UserService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        tvCartBadge = view.findViewById(R.id.tvCartBadgeNotification);
        ImageView ivUserCartNotification = view.findViewById(R.id.ivUserCartNotification);

        ivUserCartNotification.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserCartActivity.class));
        });

        ImageView ivChatNotification = view.findViewById(R.id.ivChatNotification);
        ivChatNotification.setOnClickListener(v -> {
            TokenManager tm = TokenManager.getInstance(getContext());
            if (tm.getUserId() == -1) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem tin nhắn", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.chat.UserConversationListActivity.class));
            }
        });

        // --- 1. SETUP DANH SÁCH TĨNH (Giữ nguyên code của bạn) ---
        RecyclerView rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem(android.R.drawable.ic_menu_today, "Khuyến mãi", "Thẻ quà mua cả Shopee", 16));
        list.add(new NotificationItem(android.R.drawable.ic_menu_camera, "Live & Video", "Săn voucher giảm đến 30%", 4));
        list.add(new NotificationItem(android.R.drawable.ic_menu_agenda, "Cập nhật Shopee", "Dành vài phút chia sẻ ĐỂ ĐÂY", 5));
        list.add(new NotificationItem(android.R.drawable.ic_menu_gallery, "Giải Thưởng Shopee", "Tại Đấu Trường Nối Hình Shopee", 1));
        list.add(new NotificationItem(android.R.drawable.ic_menu_mapmode, "ShopeeFood", "Tài xế hiện đang ở quán...", 5));

        NotificationAdapter adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);

        // --- 2. SETUP DANH SÁCH ĐỘNG (Cập nhật đơn hàng) ---
        rvOrderNotifications = view.findViewById(R.id.rvOrderNotifications);
        layoutEmptyOrder = view.findViewById(R.id.layoutEmptyOrder);
        rvOrderNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        tokenManager = TokenManager.getInstance(getContext());
        apiService = ApiClient.getUserService(tokenManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật giỏ hàng
        if (tvCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalQuantity();
            if (totalItems > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(totalItems > 99 ? "99+" : String.valueOf(totalItems));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }

        // Gọi API nạp thông báo thật
        loadRealNotifications();
    }

    private void loadRealNotifications() {
        long userId = tokenManager.getUserId();
        if (userId == -1) {
            rvOrderNotifications.setVisibility(View.GONE);
            layoutEmptyOrder.setVisibility(View.VISIBLE);
            return;
        }

        apiService.getMyNotifications(userId).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call, Response<List<NotificationResponse>> response) {
                // 1. Máy dò mã lỗi HTTP
                android.util.Log.e("DEBUG_NOTIF", "Mã HTTP trả về: " + response.code());

                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<NotificationResponse> notifList = response.body();

                    // 2. Máy dò số lượng dữ liệu
                    android.util.Log.e("DEBUG_NOTIF", "Số lượng thông báo nhận được: " + notifList.size());

                    if (notifList.isEmpty()) {
                        rvOrderNotifications.setVisibility(View.GONE);
                        layoutEmptyOrder.setVisibility(View.VISIBLE);
                    } else {
                        rvOrderNotifications.setVisibility(View.VISIBLE);
                        layoutEmptyOrder.setVisibility(View.GONE);

                        OrderNotificationAdapter orderAdapter = new OrderNotificationAdapter(notifList, item -> {
                            handleNotificationClick(item);
                        });
                        rvOrderNotifications.setAdapter(orderAdapter);
                    }
                } else {
                    // Nếu lỗi 403, 404, 500... sẽ nhảy vào đây
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        android.util.Log.e("DEBUG_NOTIF", "Lỗi API: " + errorBody);
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                // Nếu lỗi sập app/sai định dạng JSON sẽ nhảy vào đây
                android.util.Log.e("DEBUG_NOTIF", "Lỗi hệ thống/JSON: " + t.getMessage());
                if (isAdded()) Toast.makeText(getContext(), "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNotificationClick(NotificationResponse item) {
        // 1. Gọi API đánh dấu đã đọc (Giữ nguyên)
        apiService.markNotificationAsRead(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadRealNotifications(); // Tải lại danh sách để đổi màu nền sang trắng
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });

        // 2. Điều hướng dựa trên Loại thông báo
        if (item.getType() != null) {
            switch (item.getType()) {
                case "ORDER":
                    if (item.getRelatedId() != null && getActivity() != null) {
                        // BƯỚC QUAN TRỌNG: Mở màn hình Đơn hàng và truyền ID sang


                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);

                        // Đóng gói ID đơn hàng vào Intent để mang sang màn hình kia
                        intent.putExtra("ORDER_ID", item.getRelatedId());

                        startActivity(intent);
                    }
                    break;

                case "PROMOTION":
                    // Sau này nếu có thông báo khuyến mãi, bạn cho mở màn hình Voucher ở đây
                    Toast.makeText(getContext(), "Mở trang Khuyến mãi!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}