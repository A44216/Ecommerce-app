package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderVH extends RecyclerView.ViewHolder {

    public TextView orderId, customerName,paymentStatus, totalPrice, createdAt;
    public ImageView ivOrder;

    public SellerOrderVH(@NonNull View itemView) {
        super(itemView);

        orderId = itemView.findViewById(R.id.tvOrderId);
        customerName = itemView.findViewById(R.id.tvCustomerName);
        paymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        totalPrice = itemView.findViewById(R.id.tvTotalPrice);
        createdAt = itemView.findViewById(R.id.tvCreatedAt);
        ivOrder = itemView.findViewById(R.id.ivOrder);
    }
}