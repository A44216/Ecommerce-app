package com.example.ecommerceapp.ui.viewholder.seller.dashboard;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerTopProductVH extends RecyclerView.ViewHolder {

    private final TextView tvRank, tvName, tvPrice, tvSoldAndRevenue;
    private ImageView imgProduct;

    public SellerTopProductVH(@NonNull View itemView) {
        super(itemView);

        tvRank = itemView.findViewById(R.id.tvRank);
        tvName = itemView.findViewById(R.id.tvName);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        tvSoldAndRevenue = itemView.findViewById(R.id.tvSoldAndRevenue);
        imgProduct = itemView.findViewById(R.id.imgProduct);
    }

    public TextView getTvRank() {
        return tvRank;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }

    public TextView getTvSoldAndRevenue() {
        return tvSoldAndRevenue;
    }

    public ImageView getImgProduct() {
        return imgProduct;
    }
}
