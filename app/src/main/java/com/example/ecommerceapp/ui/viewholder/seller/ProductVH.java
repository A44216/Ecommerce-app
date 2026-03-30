package com.example.ecommerceapp.ui.viewholder.seller;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ProductVH extends RecyclerView.ViewHolder {

    TextView name, price;

    public ProductVH(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvProductName);
        price = itemView.findViewById(R.id.tvProductPrice);
    }

    public TextView getName() {
        return name;
    }

    public TextView getPrice() {
        return price;
    }

}