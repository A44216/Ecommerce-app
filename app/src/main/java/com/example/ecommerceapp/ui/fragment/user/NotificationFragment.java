package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.NotificationItem;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.adapter.user.NotificationAdapter;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);


        //Ánh xạ icon Giỏ hàng trên Header
        ImageView ivUserCartNotification = view.findViewById(R.id.ivUserCartNotification);

        //Cài đặt sự kiện Click để bay sang UserCartActivity
        ivUserCartNotification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });
        RecyclerView rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tạo dữ liệu giả giống y hệt ảnh của bạn
        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem(android.R.drawable.ic_menu_today, "Khuyến mãi", "Thẻ quà mua cả Shopee", 16));
        list.add(new NotificationItem(android.R.drawable.ic_menu_camera, "Live & Video", "Săn voucher giảm đến 30%", 4));
        list.add(new NotificationItem(android.R.drawable.ic_menu_agenda, "Cập nhật Shopee", "Dành vài phút chia sẻ ĐỂ ĐÂY", 5));
        list.add(new NotificationItem(android.R.drawable.ic_menu_gallery, "Giải Thưởng Shopee", "Tại Đấu Trường Nối Hình Shopee", 1));
        list.add(new NotificationItem(android.R.drawable.ic_menu_mapmode, "ShopeeFood", "Tài xế hiện đang ở quán...", 5));

        NotificationAdapter adapter = new NotificationAdapter(list);
        rvNotifications.setAdapter(adapter);

        return view;
    }
}