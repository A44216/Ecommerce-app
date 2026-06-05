package com.example.ecommerceapp.ui.viewholder.admin.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.TimeUtils;

public class AdminOrderVH extends RecyclerView.ViewHolder {

    public final TextView tvOrderCode;
    public final TextView tvPaymentStatus;
    public final ImageView ivOrder;
    public final TextView tvCustomerName;
    public final TextView tvUsername;
    public final TextView tvShopName;
    public final TextView tvCreatedAt;
    public final TextView tvTotalPrice;

    public AdminOrderVH(@NonNull View itemView) {
        super(itemView);
        tvOrderCode = itemView.findViewById(R.id.tvOrderCode);
        tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        ivOrder = itemView.findViewById(R.id.ivOrder);
        tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvShopName = itemView.findViewById(R.id.tvShopName);
        tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void bind(AdminOrderResponse order, Context context, View.OnClickListener onClickListener) {
        tvOrderCode.setText(order.getOrderCode() != null ? "#" + order.getOrderCode() : "N/A");
        
        if (order.getPaymentStatus() != null) {
            tvPaymentStatus.setText(order.getPaymentStatus().getLabel());
            if ("PAID".equals(order.getPaymentStatus().name())) {
                tvPaymentStatus.setBackgroundResource(R.drawable.bg_status_paid);
                tvPaymentStatus.setTextColor(ContextCompat.getColor(context,R.color.green));
            } else {
                tvPaymentStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
                tvPaymentStatus.setTextColor(ContextCompat.getColor(context,R.color.red));
            }
        } else {
            tvPaymentStatus.setText("N/A");
        }

        String fName = order.getFullName() != null && !order.getFullName().trim().isEmpty() ? order.getFullName() : "--";
        tvCustomerName.setText("Khách: " + fName);
        
        tvUsername.setText("@" + order.getUsername());
        tvShopName.setText("Shop: " + (order.getShopName() != null ? order.getShopName() : "--"));

        if (order.getCreatedAt() != null) {
            tvCreatedAt.setText("Ngày: " + TimeUtils.formatDateTime(order.getCreatedAt().toString()));
        } else {
            tvCreatedAt.setText("Ngày: --");
        }

        if (order.getTotalPrice() != null) {
            tvTotalPrice.setText(String.format("%,.0f", order.getTotalPrice()) + " đ");
        } else {
            tvTotalPrice.setText("0đ");
        }

        ImageLoader.load(
            itemView.getContext(),
            ivOrder,
            order.getImageOrder()
        );

        if (onClickListener != null) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
