package com.example.ecommerceapp.ui.adapter.seller.order;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;
import com.example.ecommerceapp.ui.viewholder.seller.order.SellerOrderVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderVH> {

    public interface OnItemClickListener {
        void onClick(SellerOrderResponse item);
    }

    private List<SellerOrderResponse> list = new ArrayList<>();

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SellerOrderResponse> data) {
        this.list = (data != null) ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerOrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_order, parent, false);

        return new SellerOrderVH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull SellerOrderVH holder, int position) {

        SellerOrderResponse item = list.get(position);

        holder.orderId.setText("#" + item.getOrderId());
        holder.customerName.setText("Khách hàng: " + item.getCustomerName());
        holder.totalPrice.setText("Tổng tiền: " +String.format("%,.0f", item.getTotalPrice()) + " đ");

        String rawDate = item.getCreatedAt(); // ví dụ: 2026-04-05T19:05:31
        if (rawDate != null && rawDate.contains("T")) {
            rawDate = rawDate.split("T")[0]; // lấy phần trước T
        }
        holder.createdAt.setText("Ngày: " + rawDate);

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.ivOrder,
                item.getImageOrder());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}