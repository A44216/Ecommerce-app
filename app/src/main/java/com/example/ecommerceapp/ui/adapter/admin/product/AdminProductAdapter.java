package com.example.ecommerceapp.ui.adapter.admin.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminProductResponse;
import com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminProductDetailActivity;
import com.example.ecommerceapp.ui.viewholder.admin.product.AdminProductVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.Objects;

public class AdminProductAdapter extends ListAdapter<AdminProductResponse, AdminProductVH> {

    public interface OnItemClickListener {
        void onItemClick(AdminProductResponse product);
    }

    private OnItemClickListener listener;

    public AdminProductAdapter() {
        super(new DiffUtil.ItemCallback<AdminProductResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminProductResponse oldItem, @NonNull AdminProductResponse newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminProductResponse oldItem, @NonNull AdminProductResponse newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                       Objects.equals(oldItem.getPrice(), newItem.getPrice()) &&
                       oldItem.getStatus() == newItem.getStatus() &&
                       Objects.equals(oldItem.getIsDeleted(), newItem.getIsDeleted()) &&
                       Objects.equals(oldItem.getStock(), newItem.getStock()) &&
                       Objects.equals(oldItem.getSoldCount(), newItem.getSoldCount());
            }
        });
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductVH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull AdminProductVH holder, int position) {
        AdminProductResponse product = getItem(position);

        holder.getTvProductName().setText(product.getName());
        holder.getTvShopName().setText(product.getShopName() != null ? "Shop: " + product.getShopName() : "Shop: N/A");
        
        holder.getTvProductPrice().setText(String.format("Giá: %,.0f đ", product.getPrice()));

        holder.getTvProductCategory().setText(
                product.getCategoryName() != null
                        ? "Danh mục: " + product.getCategoryName()
                        : "Danh mục: Khác"
        );

        holder.getTvSoldAndStock().setText("Đã bán: " + product.getSoldCount() + " | Kho: " + product.getStock());

        // Style the status
        boolean isDeleted = Boolean.TRUE.equals(product.getIsDeleted());
        if (isDeleted) {
            holder.getTvProductStatus().setText("Đã xóa");
            holder.getTvProductStatus().setBackgroundResource(R.drawable.bg_status_blocked);
            holder.getTvProductStatus().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        } else {
            holder.getTvProductStatus().setText(product.getStatus().getLabel());
            switch (product.getStatus()) {
                case PENDING:
                    holder.getTvProductStatus().setBackgroundResource(R.drawable.bg_shop_status_pending);
                    holder.getTvProductStatus().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.orange));
                    break;
                case APPROVED:
                    holder.getTvProductStatus().setBackgroundResource(R.drawable.bg_shop_status_approved);
                    holder.getTvProductStatus().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    break;
                case REJECTED:
                    holder.getTvProductStatus().setBackgroundResource(R.drawable.bg_shop_status_rejected);
                    holder.getTvProductStatus().setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                    break;
            }
        }

        ImageLoader.load(holder.itemView.getContext(), holder.getImgPathProduct(), product.getImage());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }
}
