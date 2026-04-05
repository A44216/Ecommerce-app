package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderVH extends RecyclerView.ViewHolder {

    TextView orderId, customerName, totalPrice, createdAt;

    public SellerOrderVH(@NonNull View itemView) {
        super(itemView);

        orderId = itemView.findViewById(R.id.tvOrderId);
        customerName = itemView.findViewById(R.id.tvCustomerName);
        totalPrice = itemView.findViewById(R.id.tvTotalPrice);
        createdAt = itemView.findViewById(R.id.tvCreatedAt);
    }
}