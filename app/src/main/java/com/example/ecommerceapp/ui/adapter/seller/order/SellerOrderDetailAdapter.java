package com.example.ecommerceapp.ui.adapter.seller.order;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderItemResponse;
import com.example.ecommerceapp.ui.viewholder.seller.order.SellerOrderDetailVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderDetailAdapter extends RecyclerView.Adapter<SellerOrderDetailVH> {

    public interface OnItemClickListener {
        void onItemClick(SellerOrderItemResponse item);
    }

    private List<SellerOrderItemResponse> list = new ArrayList<>();

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SellerOrderItemResponse> data) {
        this.list = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerOrderDetailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_order_detail, parent, false);
        return new SellerOrderDetailVH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull SellerOrderDetailVH holder, int position) {

        SellerOrderItemResponse item = list.get(position);

        holder.getProductId().setText("ID = " + String.valueOf(item.getProductId()));
        holder.getProductName().setText(item.getProductName());
        holder.getSubtotal().setText(String.format("%,.0f", item.getSubtotal()) + " đ");
        holder.getQuantity().setText("x" + item.getQuantity());
        holder.getTotalPrice().setText("Tổng: " + String.format("%,.0f", item.getPrice()) + " đ");

        Log.d("IMG_DEBUG", position + " - " + item.getProductImage());

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.getIvProduct(),
                item.getProductImage()
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}