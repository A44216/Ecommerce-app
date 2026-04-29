package com.example.ecommerceapp.ui.viewholder.seller.product;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerProductVH extends RecyclerView.ViewHolder {

    ImageView imgProduct;
    TextView code, name, price, category, rating, sold, status;

    public SellerProductVH(@NonNull View itemView) {
        super(itemView);

        imgProduct = itemView.findViewById(R.id.imgPathProduct);
        code = itemView.findViewById(R.id.tvProductCode);
        name = itemView.findViewById(R.id.tvProductName);
        price = itemView.findViewById(R.id.tvProductPrice);
        category = itemView.findViewById(R.id.tvProductCategory);
        rating = itemView.findViewById(R.id.tvRating);
        sold = itemView.findViewById(R.id.tvSoldAndRevenue);
        status = itemView.findViewById(R.id.tvProductStatus);
    }

    public ImageView getImgProduct() {
        return imgProduct;
    }

    public TextView getCode() { return code; }
    public TextView getName() { return name; }
    public TextView getPrice() { return price; }
    public TextView getCategory() { return category; }
    public TextView getRating() { return rating; }
    public TextView getSold() { return sold; }
    public TextView getStatus() { return status; }
}