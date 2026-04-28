package com.example.ecommerceapp.ui.viewholder.admin.management.shop;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopResponse;
import com.example.ecommerceapp.utils.NumberUtils;

public class AdminShopVH extends RecyclerView.ViewHolder {

    private final ImageView ivShopAvatar;
    private final TextView tvShopName;
    private final TextView tvShopStatus;
    private final TextView tvShopEmail;
    private final TextView tvShopPhone;
    private final TextView tvRating;
    private final TextView tvOrders;
    private final TextView tvRevenue;

    public AdminShopVH(@NonNull View itemView) {
        super(itemView);
        ivShopAvatar = itemView.findViewById(R.id.ivShopAvatar);
        tvShopName = itemView.findViewById(R.id.tvShopName);
        tvShopStatus = itemView.findViewById(R.id.tvShopStatus);
        tvShopEmail = itemView.findViewById(R.id.tvShopEmail);
        tvShopPhone = itemView.findViewById(R.id.tvShopPhone);
        tvRating = itemView.findViewById(R.id.tvRating);
        tvOrders = itemView.findViewById(R.id.tvOrders);
        tvRevenue = itemView.findViewById(R.id.tvRevenue);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void bind(AdminShopResponse shop) {
        tvShopName.setText(shop.getShopName() != null ? shop.getShopName() : "Không tên");
        tvShopEmail.setText(shop.getEmail() != null ? "Email: " + shop.getEmail() : "Email: N/A");
        tvShopPhone.setText(shop.getPhone() != null ? "SĐT: " + shop.getPhone() : "SĐT: N/A");
        
        tvRating.setText(String.format("%.1f ⭐", shop.getRatingAvg() != null ? shop.getRatingAvg().doubleValue() : 0.0));
        tvOrders.setText((shop.getTotalOrders() != null ? shop.getTotalOrders() : 0) + " Đơn");
        tvRevenue.setText(NumberUtils.formatCompact(shop.getTotalRevenue() != null ? shop.getTotalRevenue() : java.math.BigDecimal.ZERO));

        ImageLoader.load(itemView.getContext(), ivShopAvatar, shop.getAvatar());

        bindStatus(shop.getStatus());
    }

    @SuppressLint("SetTextI18n")
    private void bindStatus(ShopStatus status) {
        if (status == null) {
            tvShopStatus.setVisibility(View.GONE);
            return;
        }
        tvShopStatus.setVisibility(View.VISIBLE);
        switch (status) {
            case APPROVED:
                tvShopStatus.setText("Đã duyệt");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_approved);
                tvShopStatus.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.green));
                break;
            case PENDING:
                tvShopStatus.setText("Chờ duyệt");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                tvShopStatus.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.orange));
                break;
            case REJECTED:
                tvShopStatus.setText("Bị từ chối");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                tvShopStatus.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.red));
                break;
            case BLOCKED:
                tvShopStatus.setText("Bị khóa");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_blocked);
                tvShopStatus.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(), R.color.red));
                break;
        }
    }
}
