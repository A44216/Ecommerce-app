package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {

    private List<UserOrderResponse> orderList = new ArrayList<>();

    public void updateData(List<UserOrderResponse> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        UserOrderResponse order = orderList.get(position);

        // 1. Mã đơn hàng
        holder.tvOrderId.setText("Đơn hàng: #" + order.getId());

        // 2. Tổng tiền
        if (order.getTotalPrice() != null) {
            DecimalFormat df = new DecimalFormat("#,###");
            holder.tvOrderTotal.setText("Thành tiền: " + df.format(order.getTotalPrice()) + "đ");
        }

        // 3. Ngày đặt
        if (order.getCreatedAt() != null) {
            // Cắt bớt phần giờ/phút/giây thừa nếu ngày trả về quá dài
            String date = order.getCreatedAt().split("T")[0];
            holder.tvOrderDate.setText("Ngày đặt: " + date);
        }

        // 4. Trạng thái đơn hàng (Dịch sang tiếng Việt)
        String statusText = "Đang xử lý";
        if (order.getStatus() != null) {
            switch (order.getStatus()) {
                case "PENDING": statusText = "Chờ xác nhận"; break;
                case "SHIPPING": statusText = "Đang giao hàng"; break;
                case "DELIVERED": statusText = "Đã giao"; break;
                case "CANCELLED": statusText = "Đã hủy"; break;
                default: statusText = order.getStatus();
            }
        }
        holder.tvOrderStatus.setText(statusText);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}