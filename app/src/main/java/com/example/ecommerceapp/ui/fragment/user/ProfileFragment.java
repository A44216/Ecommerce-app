package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.utils.CartManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;

public class ProfileFragment extends Fragment {
    private TextView tvCartBadge;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ icon Giỏ hàng
        ImageView ivCartProfile = view.findViewById(R.id.ivCartProfile);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);

        ivCartProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        // 1. Ánh xạ chữ "Xem lịch sử mua hàng >" và 4 nút trạng thái
        TextView tvViewOrderHistory = view.findViewById(R.id.tvViewOrderHistory);
        View btnPending = view.findViewById(R.id.btnPending);
        View btnProcessing = view.findViewById(R.id.btnProcessing);
        View btnShipping = view.findViewById(R.id.btnShipping);
        View btnDelivered = view.findViewById(R.id.btnDelivered);

        // 2. Bắt sự kiện Click để chuyển trang & truyền Trạng thái tương ứng
        tvViewOrderHistory.setOnClickListener(v -> openOrderHistory("ALL"));

        if (btnPending != null) btnPending.setOnClickListener(v -> openOrderHistory("PENDING"));
        if (btnProcessing != null) btnProcessing.setOnClickListener(v -> openOrderHistory("PROCESSING"));
        if (btnShipping != null) btnShipping.setOnClickListener(v -> openOrderHistory("SHIPPING"));
        if (btnDelivered != null) btnDelivered.setOnClickListener(v -> openOrderHistory("DELIVERED"));


        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TokenManager tokenManager = TokenManager.getInstance(getContext());

        long currentUserId = tokenManager.getUserId();
        if (currentUserId != -1) {
            // Tạm thời hiển thị ID vì chúng ta chưa lấy được Tên từ Backend
            tvUsername.setText("Khách hàng #" + currentUserId);
        } else {
            tvUsername.setText("Chưa đăng nhập");
        }

        //  CHỨC NĂNG ĐĂNG XUẤT
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // 1. Xóa toàn bộ dữ liệu đăng nhập
            tokenManager.clearAllData();

            // 2. Xóa sạch giỏ hàng
            CartManager.getInstance().clearCart();

            // 3. Chuyển hướng về màn hình Đăng nhập & Xóa lịch sử trang cũ
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
    }
    // Hàm phụ trợ kiểm tra và hiển thị chấm đỏ
    private void updateCartBadge() {
        if (tvCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalQuantity();

            if (totalItems > 0) {
                tvCartBadge.setVisibility(View.VISIBLE); // Hiện chấm đỏ

                // Nếu > 99 thì chỉ hiện "99+" cho đỡ bị tràn viền
                if (totalItems > 99) {
                    tvCartBadge.setText("99+");
                } else {
                    tvCartBadge.setText(String.valueOf(totalItems));
                }
            } else {
                tvCartBadge.setVisibility(View.GONE); // Ẩn chấm đỏ nếu giỏ trống
            }
        }
    }

    // Hàm phụ trợ giúp chuyển trang và mang theo trạng thái (Status)
    private void openOrderHistory(String status) {
        Intent intent = new Intent(getActivity(), UserOrderHistoryActivity.class);
        intent.putExtra("ORDER_STATUS", status);
        startActivity(intent);
    }
}