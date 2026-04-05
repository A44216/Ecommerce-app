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

    private List<SellerOrderResponse> list = new ArrayList<>();

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
        holder.customerName.setText(item.getCustomerName());
        holder.totalPrice.setText(String.format("%,.0f", item.getTotalPrice()) + " đ");

        holder.createdAt.setText(item.getCreatedAt());

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.ivOrder,
                item.getImageOrder());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}