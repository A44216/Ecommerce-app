package com.example.ecommerceapp.ui.adapter.admin.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.SystemNotificationResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder> {

    private List<SystemNotificationResponse> list = new ArrayList<>();
    private Context context;

    public void setData(List<SystemNotificationResponse> data) {
        this.list = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SystemNotificationResponse notification = list.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvBody.setText(notification.getBody());
        holder.tvType.setText(notification.getType());

        if (notification.getCreatedAt() != null && notification.getCreatedAt().length() >= 16) {
            String date = notification.getCreatedAt().substring(0, 10) + " " + notification.getCreatedAt().substring(11, 16);
            holder.tvDate.setText(date);
        } else {
            holder.tvDate.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvType, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
