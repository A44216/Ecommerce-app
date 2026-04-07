package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.CategoryViewHolder> {

    private List<UserCategoryResponse> categoryList = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    public UserCategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<UserCategoryResponse> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đã đổi tên layout ở đây
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        UserCategoryResponse category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            ImageLoader.load(holder.itemView.getContext(), holder.ivCategoryIcon, category.getImageUrl());
        }

        holder.itemView.setOnClickListener(v -> {
            if (category.getId() != null) {
                listener.onCategoryClick(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}