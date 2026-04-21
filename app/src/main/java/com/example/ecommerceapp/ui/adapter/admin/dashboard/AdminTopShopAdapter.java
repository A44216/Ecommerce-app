package com.example.ecommerceapp.ui.adapter.admin.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopShopResponse;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminTopShopAdapter extends RecyclerView.Adapter<AdminTopShopAdapter.ViewHolder> {

    private List<AdminTopShopResponse> shopList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setShopList(List<AdminTopShopResponse> shopList) {
        this.shopList = shopList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_top_shop, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminTopShopResponse shop = shopList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvShopName.setText(shop.getShopName());
        holder.tvShopOrders.setText("Đơn hàng: " + NumberUtils.formatCompact(java.math.BigDecimal.valueOf(shop.getTotalOrders())));
        holder.tvShopRevenue.setText(NumberUtils.formatCompact(shop.getTotalRevenue()) + " ₫");

        ImageLoader.load(holder.itemView.getContext(), holder.ivShopAvatar, shop.getAvatar());
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivShopAvatar;
        TextView tvRank, tvShopName, tvShopOrders, tvShopRevenue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivShopAvatar = itemView.findViewById(R.id.ivShopAvatar);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvShopOrders = itemView.findViewById(R.id.tvShopOrders);
            tvShopRevenue = itemView.findViewById(R.id.tvShopRevenue);
        }
    }
}
