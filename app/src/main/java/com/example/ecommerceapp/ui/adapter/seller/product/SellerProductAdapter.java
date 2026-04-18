package com.example.ecommerceapp.ui.adapter.seller.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.viewholder.seller.product.SellerProductVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductVH> {

    private List<SellerProductResponse> list = new ArrayList<>();

    private OnProductActionListener listener;

    private String currentStatus = "";

    public void setCurrentStatus(String status) {
        this.currentStatus = status;
    }

    public void setListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SellerProductResponse> newList) {

        if (newList == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = newList;
        }

        notifyDataSetChanged();
    }

    public interface OnProductActionListener {
        void onClick(SellerProductResponse product);
        void onEdit(SellerProductResponse product);
        void onDelete(SellerProductResponse product);
        void onRestore(SellerProductResponse product);
        void onResubmit(SellerProductResponse product);

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

        SellerProductResponse product = list.get(position);

        holder.getName().setText(product.getName());
        holder.getPrice().setText(String.format("%,.0f", product.getPrice()) + " đ");

        holder.getCategory().setText(
                product.getCategoryName() != null
                        ? "Danh mục: " + product.getCategoryName()
                        : "Không có danh mục"
        );

        holder.getRating().setText("⭐ " + product.getRatingAvg());
        holder.getSold().setText("Đã bán " + product.getSoldCount());
        holder.getStatus().setText("Trạng thái: " + product.getStatus().getLabel());

        List<ProductImageResponse> images = product.getImages();

        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            imageUrl = images.get(0).getImageUrl();
        }

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.getImgProduct(),
                imageUrl
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SellerProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            v.getContext().startActivity(intent);
        });

        if ("DELETED".equals(currentStatus)) {
            holder.itemView.findViewById(R.id.ivDeleteProduct).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.ivRestoreProduct).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.ivDeleteProduct).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.ivRestoreProduct).setVisibility(View.GONE);
        }
        holder.itemView.findViewById(R.id.ivRestoreProduct).setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestore(product);
            }
        });

        if (product.getStatus() == ProductStatus.REJECTED) {
            holder.itemView.findViewById(R.id.ivResubmitProduct).setVisibility(View.VISIBLE);
        } else {
            holder.itemView.findViewById(R.id.ivResubmitProduct).setVisibility(View.GONE);
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
    }

    public void addData(List<SellerProductResponse> newList) {
        if (newList == null || newList.isEmpty()) return;

        int oldSize = list.size();
        list.addAll(newList);
        notifyItemRangeInserted(oldSize, newList.size());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void removeItem(SellerProductResponse product) {
        int position = list.indexOf(product);
        if (position != -1) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateItem(SellerProductResponse updatedProduct) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == updatedProduct.getId()) {
                list.set(i, updatedProduct);
                notifyItemChanged(i);
                return;
            }
        }
    }
}