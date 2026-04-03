package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.NotificationItem;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<NotificationItem> notifList;

    public NotificationAdapter(List<NotificationItem> notifList) {
        this.notifList = notifList;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationItem item = notifList.get(position);

        holder.ivNotifIcon.setImageResource(item.getIconResId());
        holder.tvNotifTitle.setText(item.getTitle());
        holder.tvNotifDesc.setText(item.getDescription());

        // Ẩn hiện số lượng badge
        if (item.getBadgeCount() > 0) {
            holder.tvNotifBadge.setVisibility(View.VISIBLE);
            holder.tvNotifBadge.setText(String.valueOf(item.getBadgeCount()));
        } else {
            holder.tvNotifBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return notifList != null ? notifList.size() : 0; }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotifIcon;
        TextView tvNotifTitle, tvNotifDesc, tvNotifBadge;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotifIcon = itemView.findViewById(R.id.ivNotifIcon);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifDesc = itemView.findViewById(R.id.tvNotifDesc);
            tvNotifBadge = itemView.findViewById(R.id.tvNotifBadge);
        }
    }
}