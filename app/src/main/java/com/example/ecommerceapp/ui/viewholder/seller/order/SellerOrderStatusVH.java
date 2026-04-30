package com.example.ecommerceapp.ui.viewholder.seller.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderStatusVH extends RecyclerView.ViewHolder {

    ImageView ivCircle;
    TextView tvStatus;

    public SellerOrderStatusVH(@NonNull View itemView) {
        super(itemView);

        ivCircle = itemView.findViewById(R.id.ivStatusCircle);
        tvStatus = itemView.findViewById(R.id.tvStatus);
    }

    public TextView getTvStatus() {
        return tvStatus;
    }

    public ImageView getIvCircle() {
        return ivCircle;
    }

}
