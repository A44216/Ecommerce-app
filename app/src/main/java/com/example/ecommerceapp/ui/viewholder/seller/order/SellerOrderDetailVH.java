package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderDetailVH extends RecyclerView.ViewHolder {

    ImageView ivProduct;
    TextView tvProductId, tvSold, tvCustomerName, tvTotalPrice;

    public SellerOrderDetailVH(@NonNull View itemView) {
        super(itemView);

        ivProduct = itemView.findViewById(R.id.ivProduct);
        tvProductId = itemView.findViewById(R.id.tvProductId);
        tvSold = itemView.findViewById(R.id.tvSold);
        tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }
}
