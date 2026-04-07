package com.example.ecommerceapp.ui.adapter.seller.order;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.data.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.viewholder.seller.order.SellerOrderStatusVH;

public class SellerOrderStatusAdapter extends RecyclerView.Adapter<SellerOrderStatusVH> {

    private List<OrderStatus> list = new ArrayList<>();
    private OrderStatus selectedStatus;

    public interface OnStatusChangeListener {
        void onStatusChange(OrderStatus status);
    }

    private OnStatusChangeListener listener;

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<OrderStatus> data, OrderStatus currentStatus) {
        this.list = data;
        this.selectedStatus = currentStatus;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerOrderStatusVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_order_status, parent, false);

        return new SellerOrderStatusVH(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SellerOrderStatusVH holder, int position) {

        OrderStatus status = list.get(position);

        holder.getTvStatus().setText(getStatusText(status));

        if (status == selectedStatus) {
            holder.getIvCircle().setImageResource(R.drawable.bg_status_circle_selected);
        } else {
            holder.getIvCircle().setImageResource(R.drawable.bg_status_circle_unselected);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedStatus == status) return;

            selectedStatus = status;
            notifyDataSetChanged();

            if (listener != null) {
                listener.onStatusChange(status);
            }
        });
    }

    private String getStatusText(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Chờ xác nhận";
            case CONFIRMED:
                return "Đã xác nhận";
            case SHIPPING:
                return "Đang giao hàng";
            case COMPLETED:
                return "Hoàn thành";
            default:
                return status.name();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedStatus(OrderStatus status) {
        this.selectedStatus = status;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
