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