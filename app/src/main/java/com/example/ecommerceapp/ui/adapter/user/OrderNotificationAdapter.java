package com.example.ecommerceapp.ui.adapter.user;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.NotificationResponse;
import java.util.List;

public class OrderNotificationAdapter extends RecyclerView.Adapter<OrderNotificationAdapter.ViewHolder> {

    private List<NotificationResponse> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(NotificationResponse item);
    }

    public OrderNotificationAdapter(List<NotificationResponse> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationResponse item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());

        // Tùy chỉnh format ngày tháng ở đây nếu cần
        if(item.getCreatedAt() != null) {
            holder.tvTime.setText(item.getCreatedAt().substring(0, 10)); // Tạm lấy ngày
        }

        // Nếu chưa đọc (false), bôi màu cam nhạt. Đã đọc (true), để màu trắng.
        if (!item.isRead()) {
            holder.container.setBackgroundColor(Color.parseColor("#FFF5EE")); // Cam rất nhạt
        } else {
            holder.container.setBackgroundColor(Color.WHITE);
        }

        holder.container.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvTime;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvOrderNotifTitle);
            tvBody = itemView.findViewById(R.id.tvOrderNotifBody);
            tvTime = itemView.findViewById(R.id.tvOrderNotifTime);
            container = itemView.findViewById(R.id.layoutNotificationContainer);
        }
    }
}