package com.example.ecommerceapp.ui.viewholder.admin.order;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderItemResponse;
import com.example.ecommerceapp.utils.ImageLoader;

public class AdminOrderItemVH extends RecyclerView.ViewHolder {

    public final ImageView ivProduct;
    public final TextView tvProductId;
    public final TextView tvProductName;
    public final TextView tvUnitPrice;
    public final TextView tvQuantity;
    public final TextView tvTotalPrice;

    public AdminOrderItemVH(@NonNull View itemView) {
        super(itemView);
        ivProduct = itemView.findViewById(R.id.ivProduct);
        tvProductId = itemView.findViewById(R.id.tvProductId);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void bind(AdminOrderItemResponse item) {
        tvProductId.setText(item.getProductId() != null ? "ID: " + item.getProductId() : "ID: --");
        tvProductName.setText(item.getProductName() != null ? item.getProductName() : "--");
        
        if (item.getPrice() != null) {
            tvUnitPrice.setText(String.format("%,.0f", item.getPrice()) + " đ");
        } else {
            tvUnitPrice.setText("0 đ");
        }
        
        tvQuantity.setText("x" + (item.getQuantity() != null ? item.getQuantity() : 0));

        if (item.getPrice() != null && item.getQuantity() != null) {
            java.math.BigDecimal total = item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity()));
            tvTotalPrice.setText(String.format("%,.0f", total) + " đ");
        } else {
            tvTotalPrice.setText("0 đ");
        }

        ImageLoader.load(itemView.getContext(), ivProduct, item.getProductImage());
    }
}
