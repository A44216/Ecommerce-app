package com.example.ecommerceapp.ui.adapter.seller.order;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.PaymentStatus;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;
import com.example.ecommerceapp.ui.viewholder.seller.order.SellerOrderVH;

import java.util.Objects;

public class SellerOrderAdapter extends ListAdapter<SellerOrderResponse, SellerOrderVH> {

    public interface OnItemClickListener {
        void onClick(SellerOrderResponse item);
    }

    private OnItemClickListener listener;

    public SellerOrderAdapter() {
        super(new DiffUtil.ItemCallback<SellerOrderResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull SellerOrderResponse oldItem, @NonNull SellerOrderResponse newItem) {
                return Objects.equals(oldItem.getOrderId(), newItem.getOrderId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SellerOrderResponse oldItem, @NonNull SellerOrderResponse newItem) {
                return Objects.equals(oldItem.getOrderId(), newItem.getOrderId()) &&
                       oldItem.getStatus() == newItem.getStatus() &&
                       oldItem.getPaymentStatus() == newItem.getPaymentStatus() &&
                       Objects.equals(oldItem.getSellerRevenue(), newItem.getSellerRevenue());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        SellerOrderResponse item = getItem(position);

        holder.orderCode.setText("#" + item.getOrderCode());
        holder.customerName.setText(item.getCustomerName());
        holder.paymentStatus.setText(item.getPaymentStatus().getLabel());
        if (item.getPaymentStatus() == PaymentStatus.PAID) {
            holder.paymentStatus.setBackgroundResource(R.drawable.bg_status_paid);
            holder.paymentStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.paymentStatus.setBackgroundResource(R.drawable.bg_status_unpaid);
            holder.paymentStatus.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }

        holder.totalPrice.setText(String.format("%,.0f", item.getSellerRevenue()) + " đ");

        String rawDate = item.getCreatedAt();
        if (rawDate != null && rawDate.contains("T")) {
            rawDate = rawDate.split("T")[0];
        }
        holder.createdAt.setText(rawDate);

        String phone = item.getCustomerPhone();
        holder.customerPhone.setText("SDT: " + (phone != null ? phone : "--"));

        com.example.ecommerceapp.utils.ImageLoader.load(
                holder.itemView.getContext(),
                holder.ivOrder,
                item.getImageOrder());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item);
            }
        });
    }
}