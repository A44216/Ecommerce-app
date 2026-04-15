package com.example.ecommerceapp.ui.adapter.admin.category;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.profile.CategoryAdminResponse;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    private List<CategoryAdminResponse> list;
    private final OnActionListener listener;

    private boolean isDeletedTab;

    public interface OnActionListener {
        void onEdit(CategoryAdminResponse category);
        void onDelete(CategoryAdminResponse category);
        void onRestore(CategoryAdminResponse category);
    }

    public AdminCategoryAdapter(List<CategoryAdminResponse> list, OnActionListener listener, boolean isDeletedTab) {
        this.list = list;
        this.listener = listener;
        this.isDeletedTab = isDeletedTab;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CategoryAdminResponse> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!isDeletedTab) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivRestore.setVisibility(View.GONE);
        } else {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.GONE);
            holder.ivRestore.setVisibility(View.VISIBLE);
        }

        CategoryAdminResponse item = list.get(position);

        holder.tvName.setText(item.getName());

        holder.ivEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.ivDelete.setOnClickListener(v -> listener.onDelete(item));
        holder.ivRestore.setOnClickListener(v -> listener.onRestore(item));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivEdit, ivDelete, ivRestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivRestore = itemView.findViewById(R.id.ivRestore);
        }
    }
}