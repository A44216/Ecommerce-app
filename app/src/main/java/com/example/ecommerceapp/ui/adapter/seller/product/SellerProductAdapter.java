package com.example.ecommerceapp.ui.adapter.seller.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.viewholder.seller.product.SellerProductVH;

import java.util.List;
import java.util.Objects;

public class SellerProductAdapter extends ListAdapter<SellerProductResponse, SellerProductVH> {

    public interface OnProductActionListener {
        void onClick(SellerProductResponse product);
        void onEdit(SellerProductResponse product);
        void onDelete(SellerProductResponse product);
        void onRestore(SellerProductResponse product);
        void onResubmit(SellerProductResponse product);
    }

    private OnProductActionListener listener;

    public SellerProductAdapter() {
        super(new DiffUtil.ItemCallback<SellerProductResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull SellerProductResponse oldItem, @NonNull SellerProductResponse newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SellerProductResponse oldItem, @NonNull SellerProductResponse newItem) {
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                       Objects.equals(oldItem.getPrice(), newItem.getPrice()) &&
                       oldItem.getStatus() == newItem.getStatus() &&
                       Objects.equals(oldItem.getIsDeleted(), newItem.getIsDeleted()) &&
                       oldItem.getSoldCount() == newItem.getSoldCount();
            }
        });
    }

    public void setListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SellerProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_product_seller, parent, false);
        return new SellerProductVH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull SellerProductVH holder, int position) {
        SellerProductResponse product = getItem(position);

        holder.getCode().setText(product.getProductCode() != null ? product.getProductCode() : "N/A");
        holder.getName().setText(product.getName());
        holder.getPrice().setText(String.format("%,.0f", product.getPrice()) + " đ");

        holder.getCategory().setText(
                product.getCategoryName() != null
                        ? "Danh mục: " + product.getCategoryName()
                        : "Không có danh mục"
        );

        holder.getRating().setText("⭐ " + product.getRatingAvg());
        holder.getSold().setText("Đã bán " + product.getSoldCount());
        // Style the status
        boolean isDeleted = Boolean.TRUE.equals(product.getIsDeleted());
        if (isDeleted) {
            holder.getStatus().setText("Đã xóa");
            holder.getStatus().setBackgroundResource(R.drawable.bg_status_blocked);
            holder.getStatus().setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        } else {
            holder.getStatus().setText(product.getStatus().getLabel());
            switch (product.getStatus()) {
                case PENDING:
                    holder.getStatus().setBackgroundResource(R.drawable.bg_shop_status_pending);
                    holder.getStatus().setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.orange));
                    break;
                case APPROVED:
                    holder.getStatus().setBackgroundResource(R.drawable.bg_shop_status_approved);
                    holder.getStatus().setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    break;
                case REJECTED:
                    holder.getStatus().setBackgroundResource(R.drawable.bg_shop_status_rejected);
                    holder.getStatus().setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                    break;
            }
        }

        List<ProductImageResponse> images = product.getImages();

        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            imageUrl = images.get(0).getImageUrl();
        }

        com.example.ecommerceapp.utils.ImageLoader.load(
                holder.itemView.getContext(),
                holder.getImgProduct(),
                imageUrl
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SellerProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });

        View ivDelete = holder.itemView.findViewById(R.id.ivDeleteProduct);
        View ivRestore = holder.itemView.findViewById(R.id.ivRestoreProduct);
        View ivEdit = holder.itemView.findViewById(R.id.ivEditProduct);
        View ivResubmit = holder.itemView.findViewById(R.id.ivResubmitProduct);

        if (isDeleted) {
            ivDelete.setVisibility(View.GONE);
            ivRestore.setVisibility(View.VISIBLE);
            ivEdit.setVisibility(View.GONE);
            ivResubmit.setVisibility(View.GONE);
        } else {
            ivDelete.setVisibility(View.VISIBLE);
            ivRestore.setVisibility(View.GONE);
            ivEdit.setVisibility(View.VISIBLE);

            if (product.getStatus() == ProductStatus.REJECTED) {
                ivResubmit.setVisibility(View.VISIBLE);
            } else {
                ivResubmit.setVisibility(View.GONE);
            }
        }

        holder.itemView.findViewById(R.id.ivResubmitProduct).setOnClickListener(v -> {
            if (listener != null) {
                listener.onResubmit(product);
            }
        });

        holder.itemView.findViewById(R.id.ivEditProduct).setOnClickListener(v -> {
            if (listener != null) listener.onEdit(product);
        });

        holder.itemView.findViewById(R.id.ivDeleteProduct).setOnClickListener(v -> {
            if (listener != null) listener.onDelete(product);
        });

        holder.itemView.findViewById(R.id.ivRestoreProduct).setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestore(product);
            }
        });
    }
}