package com.example.ecommerceapp.ui.adapter.admin.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserResponse;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserVH> {

    private List<AdminUserResponse> users = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AdminUserResponse user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(List<AdminUserResponse> newUsers) {
        if (newUsers != null) {
            this.users = newUsers;
        } else {
            this.users = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminUserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserVH holder, int position) {
        AdminUserResponse user = users.get(position);
        holder.bind(user);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
