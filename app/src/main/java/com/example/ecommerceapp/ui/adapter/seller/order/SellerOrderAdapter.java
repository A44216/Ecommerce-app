package com.example.ecommerceapp.ui.adapter.seller.order;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.SellerOrderResponse;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.VH> {

    private List<SellerOrderResponse> list = new ArrayList<>();

    public void setData(List<SellerOrderResponse> data) {
        this.list = (data != null) ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_order, parent, false);

        return new VH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        SellerOrderResponse item = list.get(position);

        holder.orderId.setText("#" + item.getOrderId());
        holder.customerName.setText(item.getCustomerName());
        holder.totalPrice.setText(String.format("%,.0f", item.getTotalPrice()) + " đ");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView orderId, customerName, totalPrice;

        public VH(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.tvOrderId);
            customerName = itemView.findViewById(R.id.tvCustomerName);
            totalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}