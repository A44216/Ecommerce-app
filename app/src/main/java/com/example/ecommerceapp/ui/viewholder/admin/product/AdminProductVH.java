package com.example.ecommerceapp.ui.viewholder.admin.product;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class AdminProductVH extends RecyclerView.ViewHolder {

    private final ImageView imgPathProduct;
    private final TextView tvProductCode;
    private final TextView tvProductName;
    private final TextView tvShopName;
    private final TextView tvProductPrice;
    private final TextView tvProductCategory;
    private final TextView tvSoldAndStock;
    private final TextView tvProductStatus;

    public AdminProductVH(@NonNull View itemView) {
        super(itemView);
        imgPathProduct = itemView.findViewById(R.id.imgPathProduct);
        tvProductCode = itemView.findViewById(R.id.tvProductCode);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvShopName = itemView.findViewById(R.id.tvShopName);
        tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
        tvSoldAndStock = itemView.findViewById(R.id.tvSoldAndStock);
        tvProductStatus = itemView.findViewById(R.id.tvProductStatus);
    }

    public ImageView getImgPathProduct() { return imgPathProduct; }
    public TextView getTvProductCode() { return tvProductCode; }
    public TextView getTvProductName() { return tvProductName; }
    public TextView getTvShopName() { return tvShopName; }
    public TextView getTvProductPrice() { return tvProductPrice; }
    public TextView getTvProductCategory() { return tvProductCategory; }
    public TextView getTvSoldAndStock() { return tvSoldAndStock; }
    public TextView getTvProductStatus() { return tvProductStatus; }
}
