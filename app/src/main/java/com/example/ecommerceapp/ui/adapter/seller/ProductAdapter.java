package com.example.ecommerceapp.ui.adapter.seller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.ui.viewholder.seller.ProductVH;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductVH> {

    private List<ProductResponse> list = new ArrayList<>();

    public void setData(List<ProductResponse> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_seller, parent, false);
        return new ProductVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        ProductResponse product = list.get(position);

        holder.getName().setText(product.getName());
        holder.getPrice().setText(String.valueOf(product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}