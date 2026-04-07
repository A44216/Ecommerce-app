package com.example.ecommerceapp.ui.adapter.seller.product;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.ui.viewholder.seller.product.SellerProductVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductVH> {

    private List<SellerProductResponse> list = new ArrayList<>();

    private OnProductActionListener listener;

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

        // ===== TEXT =====
        holder.getName().setText(product.getName());
        holder.getPrice().setText(String.format("%,.0f", product.getPrice()) + " đ");

        holder.getCategory().setText(
                product.getCategoryName() != null
                        ? "Danh mục: " + product.getCategoryName()
                        : "Không có danh mục"
        );

        holder.getRating().setText("⭐ " + product.getRatingAvg());

        holder.getSold().setText("Đã bán " + product.getSoldCount());

        holder.getStatus().setText("Trạng thái: " + product.getStatus());

        // Ảnh sản phẩm
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

        // Click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(product);
        });

        // Edit
        holder.itemView.findViewById(R.id.ivEditProduct).setOnClickListener(v -> {
            if (listener != null) listener.onEdit(product);
        });

        // Delete
        holder.itemView.findViewById(R.id.ivDeleteProduct).setOnClickListener(v -> {
            if (listener != null) listener.onDelete(product);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

}