package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderDetailVH extends RecyclerView.ViewHolder {

    private ImageView ivProduct;
    private TextView tvProductCode, tvProductName, tvQuantity, tvUnitPrice, tvTotalPrice;

    public SellerOrderDetailVH(@NonNull View itemView) {
        super(itemView);

        ivProduct = itemView.findViewById(R.id.ivProduct);
        tvProductCode = itemView.findViewById(R.id.tvProductCode);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }

    public ImageView getIvProduct() {
        return ivProduct;
    }

    public TextView getProductCode() {
        return tvProductCode;
    }

    public TextView getProductName() {
        return tvProductName;
    }

    public TextView getQuantity() {
        return tvQuantity;
    }

    public TextView getUnitPrice() {
        return tvUnitPrice;
    }

    public TextView getTvTotalPrice() {
        return tvTotalPrice;
    }
}