package com.example.ecommerceapp.ui.adapter.user;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserOrderItemResponse;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;
import com.example.ecommerceapp.utils.ImageLoader;

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
        DecimalFormat df = new DecimalFormat("#,###");

        // 1. Gắn Mã đơn, Tiền, Ngày tháng (Như cũ)
        holder.tvOrderId.setText("Đơn hàng: #" + order.getId());
        if (order.getTotalPrice() != null) {
            holder.tvOrderTotal.setText("Thành tiền: " + df.format(order.getTotalPrice()) + "đ");
        }
        if (order.getCreatedAt() != null) {
            String date = order.getCreatedAt().split("T")[0];
            holder.tvOrderDate.setText("Ngày đặt: " + date);
        }

        // 2. Xử lý Trạng thái (Như cũ)
        String statusText = "Đang xử lý";
        holder.tvOrderStatus.setTextColor(Color.parseColor("#EE4D2D"));
        if (order.getStatus() != null) {
            switch (order.getStatus()) {
                case "PENDING": statusText = "Chờ xác nhận"; break;
                case "PROCESSING": statusText = "Chờ lấy hàng"; break;
                case "SHIPPING": statusText = "Đang giao hàng"; break;
                case "DELIVERED":
                    statusText = "Đã giao";
                    holder.tvOrderStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case "CANCELLED":
                    statusText = "Đã hủy";
                    holder.tvOrderStatus.setTextColor(Color.parseColor("#F44336"));
                    break;
                default: statusText = order.getStatus();
            }
        }
        holder.tvOrderStatus.setText(statusText);

        // ==========================================
        // 3. BƠM DANH SÁCH SẢN PHẨM VÀO CONTAINER
        // ==========================================
        // Dọn dẹp sạch sẽ rác cũ trước khi bơm (Rất quan trọng trong RecyclerView)
        holder.llOrderItemsContainer.removeAllViews();

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

            for (UserOrderItemResponse item : order.getOrderItems()) {
                // Tạo một View "Món đồ" từ file XML item_user_order_product
                View productView = inflater.inflate(R.layout.item_user_order_product, holder.llOrderItemsContainer, false);

                // Ánh xạ các thành phần bên trong View "Món đồ" đó
                ImageView ivProductImage = productView.findViewById(R.id.ivProductImage);
                TextView tvProductName = productView.findViewById(R.id.tvProductName);
                TextView tvProductPrice = productView.findViewById(R.id.tvProductPrice);
                TextView tvProductQuantity = productView.findViewById(R.id.tvProductQuantity);

                // Đổ dữ liệu vào
                tvProductName.setText(item.getProductName());
                tvProductQuantity.setText("x" + item.getQuantity());

                if (item.getPrice() != null) {
                    tvProductPrice.setText(df.format(item.getPrice()) + "đ");
                }

                // --- SỬ DỤNG IMAGELOADER CỦA BẠN ĐỂ LOAD ẢNH ---
                if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                    // Nhớ import com.example.ecommerceapp.utils.ImageLoader; ở đầu file nhé!
                    ImageLoader.load(holder.itemView.getContext(), ivProductImage, item.getProductImage());
                }
                // -----------------------------------------------

                //nhét View "Món đồ" này vào khung Container của thẻ Đơn hàng
                holder.llOrderItemsContainer.addView(productView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvOrderTotal;
        LinearLayout llOrderItemsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            llOrderItemsContainer = itemView.findViewById(R.id.llOrderItemsContainer); // ÁNH XẠ NÓ
        }
    }
}