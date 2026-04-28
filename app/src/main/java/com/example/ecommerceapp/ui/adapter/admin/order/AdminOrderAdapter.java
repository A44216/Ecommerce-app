package com.example.ecommerceapp.ui.adapter.admin.order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.ui.viewholder.admin.order.AdminOrderVH;

public class AdminOrderAdapter extends ListAdapter<AdminOrderResponse, AdminOrderVH> {

    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AdminOrderResponse order);
    }

    public AdminOrderAdapter(Context context, OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<AdminOrderResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminOrderResponse oldItem, @NonNull AdminOrderResponse newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminOrderResponse oldItem, @NonNull AdminOrderResponse newItem) {
                return oldItem.getStatus() == newItem.getStatus() &&
                       oldItem.getPaymentStatus() == newItem.getPaymentStatus() &&
                       (oldItem.getTotalPrice() == null ? newItem.getTotalPrice() == null : oldItem.getTotalPrice().equals(newItem.getTotalPrice()));
            }
        });
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminOrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new AdminOrderVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderVH holder, int position) {
        AdminOrderResponse order = getItem(position);
        holder.bind(order, context, v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getItem(pos));
            }
        });
    }
}
