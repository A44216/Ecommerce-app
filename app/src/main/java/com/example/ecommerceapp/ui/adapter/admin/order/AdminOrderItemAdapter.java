package com.example.ecommerceapp.ui.adapter.admin.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderItemResponse;
import com.example.ecommerceapp.ui.viewholder.admin.order.AdminOrderItemVH;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderItemAdapter extends RecyclerView.Adapter<AdminOrderItemVH> {

    private List<AdminOrderItemResponse> items = new ArrayList<>();

    public void setData(List<AdminOrderItemResponse> items) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminOrderItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order_detail, parent, false);
        return new AdminOrderItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderItemVH holder, int position) {
        AdminOrderItemResponse item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
