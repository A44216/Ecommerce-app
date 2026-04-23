package com.example.ecommerceapp.ui.adapter.admin.management.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserResponse;
import com.example.ecommerceapp.ui.viewholder.admin.management.user.AdminUserVH;

import java.util.Objects;

public class AdminUserAdapter extends ListAdapter<AdminUserResponse, AdminUserVH> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AdminUserResponse user);
    }

    public AdminUserAdapter() {
        super(new DiffUtil.ItemCallback<AdminUserResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminUserResponse oldItem, @NonNull AdminUserResponse newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminUserResponse oldItem, @NonNull AdminUserResponse newItem) {
                return oldItem.getStatus() == newItem.getStatus() &&
                       Objects.equals(oldItem.getFullName(), newItem.getFullName());
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminUserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserVH holder, int position) {
        AdminUserResponse user = getItem(position);
        holder.bind(user);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }
    }


