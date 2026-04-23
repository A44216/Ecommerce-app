package com.example.ecommerceapp.ui.adapter.admin.management.shop;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopResponse;
import com.example.ecommerceapp.ui.viewholder.admin.management.shop.AdminShopVH;

import java.util.Objects;

public class AdminShopAdapter extends ListAdapter<AdminShopResponse, AdminShopVH> {

    private OnShopClickListener listener;

    public interface OnShopClickListener {
        void onShopClick(AdminShopResponse shop);
    }

    public AdminShopAdapter(OnShopClickListener listener) {
        super(new DiffUtil.ItemCallback<AdminShopResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminShopResponse oldItem, @NonNull AdminShopResponse newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminShopResponse oldItem, @NonNull AdminShopResponse newItem) {
                return oldItem.getStatus() == newItem.getStatus() &&
                       Objects.equals(oldItem.getShopName(), newItem.getShopName());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminShopVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_shop, parent, false);
        return new AdminShopVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminShopVH holder, int position) {
        AdminShopResponse shop = getItem(position);
        holder.bind(shop);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShopClick(shop);
            }
        });
    }
}
