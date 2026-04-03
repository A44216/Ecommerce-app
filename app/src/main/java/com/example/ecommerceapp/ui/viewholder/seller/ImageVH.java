package com.example.ecommerceapp.ui.viewholder.seller;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ImageVH extends RecyclerView.ViewHolder{
    private final ImageView imgProduct;
    private final ImageView btnDelete;

    public ImageVH(@NonNull View itemView) {
        super(itemView);

        imgProduct = itemView.findViewById(R.id.imgProduct);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }

    public ImageView getImgProduct() {
        return imgProduct;
    }

    public ImageView getBtnDelete() {
        return btnDelete;
    }
}
