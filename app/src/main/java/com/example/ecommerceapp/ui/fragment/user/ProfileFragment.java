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
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;

import com.example.ecommerceapp.R;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ icon Giỏ hàng
        ImageView ivCartProfile = view.findViewById(R.id.ivCartProfile);

        // Bắt sự kiện chuyển trang
        ivCartProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        //Ánh xạ chữ "Xem lịch sử mua hàng >" (Bạn nhớ thay đúng ID của bạn nhé)
        TextView tvViewOrderHistory = view.findViewById(R.id.tvViewOrderHistory);

        //Bắt sự kiện Click để chuyển trang
        tvViewOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }
}