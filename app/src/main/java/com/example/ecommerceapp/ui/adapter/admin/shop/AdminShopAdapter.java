package com.example.ecommerceapp.ui.adapter.admin.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.shop.AdminShopResponse;
import com.example.ecommerceapp.ui.viewholder.admin.shop.AdminShopVH;

import java.util.ArrayList;
import java.util.List;

public class AdminShopAdapter extends RecyclerView.Adapter<AdminShopVH> {

    private final List<AdminShopResponse> shopList = new ArrayList<>();
    private final OnShopClickListener listener;

    public interface OnShopClickListener {
        void onShopClick(AdminShopResponse shop);
    }

    public AdminShopAdapter(OnShopClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<AdminShopResponse> newShops) {
        // Simple update, for better performance consider DiffUtil
        shopList.clear();
        if (newShops != null) {
            shopList.addAll(newShops);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminShopVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_shop, parent, false);
        return new AdminShopVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShopVH holder, int position) {
        AdminShopResponse shop = shopList.get(position);
        holder.bind(shop);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShopClick(shop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }
}
