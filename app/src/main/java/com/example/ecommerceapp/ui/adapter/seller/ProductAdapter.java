package com.example.ecommerceapp.ui.adapter.seller;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.ui.viewholder.seller.ProductVH;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductVH> {

    private List<ProductResponse> list = new ArrayList<>();
    private OnProductActionListener listener;

    public void setListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ProductResponse> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public interface OnProductActionListener {
        void onClick(ProductResponse product);
        void onEdit(ProductResponse product);
        void onDelete(ProductResponse product);
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_seller, parent, false);
        return new ProductVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        ProductResponse product = list.get(position);

        // ===== TEXT =====
        holder.getName().setText(product.getName());
        holder.getPrice().setText(product.getPrice() + " đ");

        holder.getCategory().setText(
                product.getCategoryName() != null
                        ? product.getCategoryName()
                        : "Không có danh mục"
        );

        holder.getRating().setText("⭐ " + product.getRatingAvg());

        holder.getSold().setText("Đã bán " + product.getSoldCount());

        holder.getStatus().setText("Trạng thái: " + product.getStatus());

        // Ảnh sản phẩm
        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.getImgProduct());

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
        return list.size();
    }
}