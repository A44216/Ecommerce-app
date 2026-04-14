package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderDetailVH extends RecyclerView.ViewHolder {

    private ImageView ivProduct;
    private TextView tvProductId, tvProductName, tvQuantity, tvTotalPrice, tvSubtotal;

    public SellerOrderDetailVH(@NonNull View itemView) {
        super(itemView);

        ivProduct = itemView.findViewById(R.id.ivProduct);
        tvProductId = itemView.findViewById(R.id.tvProductId);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }

    public ImageView getIvProduct() {
        return ivProduct;
    }

    public TextView getProductId() {
        return tvProductId;
    }

    public TextView getProductName() {
        return tvProductName;
    }

    public TextView getQuantity() {
        return tvQuantity;
    }

    public TextView getTotalPrice() {
        return tvTotalPrice;
    }

    public TextView getSubtotal() {
        return tvSubtotal;
    }
}