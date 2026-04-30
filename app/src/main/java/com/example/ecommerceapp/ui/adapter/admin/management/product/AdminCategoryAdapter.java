package com.example.ecommerceapp.ui.adapter.admin.management.product;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminCategoryResponse;

import java.util.Objects;

public class AdminCategoryAdapter extends ListAdapter<AdminCategoryResponse, AdminCategoryAdapter.ViewHolder> {

    private final OnActionListener listener;
    private boolean isDeletedTab;

    public interface OnActionListener {
        void onEdit(AdminCategoryResponse category);
        void onDelete(AdminCategoryResponse category);
        void onRestore(AdminCategoryResponse category);
    }

    public AdminCategoryAdapter(OnActionListener listener, boolean isDeletedTab) {
        super(new DiffUtil.ItemCallback<AdminCategoryResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminCategoryResponse oldItem, @NonNull AdminCategoryResponse newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminCategoryResponse oldItem, @NonNull AdminCategoryResponse newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                       Objects.equals(oldItem.getIsDeleted(), newItem.getIsDeleted());
            }
        });
        this.listener = listener;
        this.isDeletedTab = isDeletedTab;
    }

    public void setDeletedTab(boolean deletedTab) {
        this.isDeletedTab = deletedTab;
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

        AdminCategoryResponse item = getItem(position);

        holder.tvName.setText(item.getName());

        holder.ivEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.ivDelete.setOnClickListener(v -> listener.onDelete(item));
        holder.ivRestore.setOnClickListener(v -> listener.onRestore(item));
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