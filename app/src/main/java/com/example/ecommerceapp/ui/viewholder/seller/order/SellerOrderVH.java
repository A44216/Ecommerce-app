package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderVH extends RecyclerView.ViewHolder {

    public TextView orderCode, customerName, customerPhone, paymentStatus, totalPrice, createdAt;
    public ImageView ivOrder;

    public SellerOrderVH(@NonNull View itemView) {
        super(itemView);

        orderCode = itemView.findViewById(R.id.tvOrderCode);
        customerName = itemView.findViewById(R.id.tvCustomerName);
        customerPhone = itemView.findViewById(R.id.tvCustomerPhone);
        paymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        totalPrice = itemView.findViewById(R.id.tvSellerRevenue);
        createdAt = itemView.findViewById(R.id.tvCreatedAt);
        ivOrder = itemView.findViewById(R.id.ivOrder);
    }
}