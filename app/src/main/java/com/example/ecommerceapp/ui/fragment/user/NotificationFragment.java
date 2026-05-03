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
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.OrderDetailActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.ui.adapter.user.NotificationAdapter;
import com.example.ecommerceapp.ui.adapter.user.OrderNotificationAdapter;
import com.example.ecommerceapp.utils.CartManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private TextView tvCartBadge;
    private RecyclerView rvOrderNotifications;
    private LinearLayout layoutEmptyOrder;
    private TokenManager tokenManager;
    private UserService apiService;
    
    private List<NotificationItem> listTopNotifications;
    private NotificationAdapter topAdapter;

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
                showLoginRequireDialog();
            } else {
                startActivity(new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.chat.UserConversationListActivity.class));
            }
        });

        // --- 1. SETUP DANH SÁCH TĨNH (Giữ nguyên code của bạn) ---
        RecyclerView rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        listTopNotifications = new ArrayList<>();
        listTopNotifications.add(new NotificationItem(android.R.drawable.ic_menu_today, "Khuyến mãi", "Chưa có thông báo mới", 0));
        listTopNotifications.add(new NotificationItem(android.R.drawable.ic_menu_camera, "Live & Video", "Chưa có thông báo mới", 0));
        listTopNotifications.add(new NotificationItem(android.R.drawable.ic_menu_agenda, "Cập nhật hệ thống", "Chưa có thông báo mới", 0));
        listTopNotifications.add(new NotificationItem(android.R.drawable.ic_menu_gallery, "Giải thưởng & Quà tặng", "Chưa có thông báo mới", 0));
        listTopNotifications.add(new NotificationItem(android.R.drawable.ic_menu_mapmode, "Giao đồ ăn", "Chưa có thông báo mới", 0));
 
        topAdapter = new NotificationAdapter(listTopNotifications, item -> {
            String type = "";
            switch (item.getTitle()) {
                case "Khuyến mãi": type = "PROMOTION"; break;
                case "Live & Video": type = "LIVE"; break;
                case "Cập nhật hệ thống": type = "SYSTEM"; break;
                case "Giải thưởng & Quà tặng": type = "AWARDS"; break;
                case "Giao đồ ăn": type = "FOOD"; break;
            }
            if (!type.isEmpty()) {
                Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.notification.CategoryNotificationActivity.class);
                intent.putExtra("NOTIFICATION_TYPE", type);
                intent.putExtra("NOTIFICATION_TITLE", item.getTitle());
                startActivity(intent);
            }
        });
        rvNotifications.setAdapter(topAdapter);

        // --- 2. SETUP DANH SÁCH ĐỘNG (Cập nhật đơn hàng) ---
        rvOrderNotifications = view.findViewById(R.id.rvOrderNotifications);
        layoutEmptyOrder = view.findViewById(R.id.layoutEmptyOrder);
        rvOrderNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        tokenManager = TokenManager.getInstance(getContext());
        apiService = ApiClient.getUserService(tokenManager);

        return view;
    }
    
    private void showLoginRequireDialog() {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này. Đi đến trang đăng nhập ngay?")
                .setCancelable(true)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
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
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<NotificationResponse> allNotifications = response.body();

                    // 1. Lọc thông báo Đơn hàng cho danh sách dưới
                    List<NotificationResponse> orderNotifs = new ArrayList<>();
                    // 2. Map để lưu nội dung mới nhất của từng loại cho danh sách trên
                    Map<String, NotificationResponse> latestByType = new HashMap<>();
                    // 3. Map để đếm số lượng chưa đọc theo loại
                    Map<String, Integer> unreadCounts = new HashMap<>();

                    for (NotificationResponse n : allNotifications) {
                        String type = n.getType();
                        if ("ORDER".equals(type)) {
                            orderNotifs.add(n);
                        } else {
                            // Lưu thông báo đầu tiên (mới nhất) tìm thấy cho mỗi loại
                            if (!latestByType.containsKey(type)) {
                                latestByType.put(type, n);
                            }
                        }

                        // Đếm số lượng chưa đọc
                        if (!n.isRead()) {
                            unreadCounts.put(type, unreadCounts.getOrDefault(type, 0) + 1);
                        }
                    }

                    // --- CẬP NHẬT DANH SÁCH TRÊN (5 mục) ---
                    updateTopCategory("Khuyến mãi", "PROMOTION", latestByType, unreadCounts);
                    updateTopCategory("Live & Video", "LIVE", latestByType, unreadCounts);
                    updateTopCategory("Cập nhật hệ thống", "SYSTEM", latestByType, unreadCounts);
                    updateTopCategory("Giải thưởng & Quà tặng", "AWARDS", latestByType, unreadCounts);
                    updateTopCategory("Giao đồ ăn", "FOOD", latestByType, unreadCounts);
                    topAdapter.notifyDataSetChanged();

                    // --- CẬP NHẬT DANH SÁCH DƯỚI (Cập nhật đơn hàng) ---
                    if (orderNotifs.isEmpty()) {
                        rvOrderNotifications.setVisibility(View.GONE);
                        layoutEmptyOrder.setVisibility(View.VISIBLE);
                    } else {
                        rvOrderNotifications.setVisibility(View.VISIBLE);
                        layoutEmptyOrder.setVisibility(View.GONE);
                        OrderNotificationAdapter orderAdapter = new OrderNotificationAdapter(orderNotifs, item -> {
                            handleNotificationClick(item);
                        });
                        rvOrderNotifications.setAdapter(orderAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                if (isAdded()) Toast.makeText(getContext(), "Lỗi tải thông báo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTopCategory(String title, String type, Map<String, NotificationResponse> latestByType, Map<String, Integer> unreadCounts) {
        for (NotificationItem item : listTopNotifications) {
            if (item.getTitle().equals(title)) {
                // Cập nhật nội dung mới nhất
                NotificationResponse latest = latestByType.get(type);
                if (latest != null) {
                    item.setDescription(latest.getTitle()); // Hoặc latest.getBody() tùy bạn muốn hiện gì
                }
                // Cập nhật số lượng chưa đọc
                item.setBadgeCount(unreadCounts.getOrDefault(type, 0));
                break;
            }
        }
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
                        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                        intent.putExtra("ORDER_ID", item.getRelatedId());
                        startActivity(intent);
                    }
                    break;

                case "SHOP":
                    if (getActivity() != null) {
                        // Nếu là thông báo shop, có thể mở màn hình chi tiết shop hoặc thông báo trạng thái
                        Toast.makeText(getContext(), item.getBody(), Toast.LENGTH_LONG).show();
                        // Có thể điều hướng đến Seller MainActivity nếu shop đã được duyệt
                        if (item.getTitle().contains("duyệt")) {
                             // User có thể cần logout/login lại để cập nhật Role, 
                             // hoặc ta chỉ hiện thông báo hướng dẫn.
                        }
                    }
                    break;

                case "PROMOTION":
                    // Sau này nếu có thông báo khuyến mãi, bạn cho mở màn hình Voucher ở đây
                    Toast.makeText(getContext(), "Mở trang Khuyến mãi!", Toast.LENGTH_SHORT).show();
                    break;
                case "SYSTEM":
                    Toast.makeText(getContext(), item.getBody(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}