package com.example.ecommerceapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.ui.activity.home.user.product.UserProductDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

public class ProductCarouselAdapter extends RecyclerView.Adapter<ProductCarouselAdapter.ViewHolder> {

    private final Context context;
    private final List<ProductResponse> productList;

    public ProductCarouselAdapter(Context context, List<ProductResponse> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assistant_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        
        holder.tvProductName.setText(product.getName());
        
        if (product.getPrice() != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            holder.tvProductPrice.setText(formatter.format(product.getPrice()) + "đ");
        } else {
            holder.tvProductPrice.setText("0đ");
        }

        String finalImageUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            finalImageUrl = product.getImages().get(0).getImageUrl();
            Glide.with(context)
                 .load(finalImageUrl)
                 .placeholder(android.R.drawable.ic_menu_gallery)
                 .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        String finalImageUrlParam = finalImageUrl;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProductDetailActivity.class);
            if (product.getId() != null) {
                intent.putExtra("product_id", product.getId());
            }
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice() != null ? product.getPrice().toString() : "0");
            if (finalImageUrlParam != null) {
                intent.putExtra("product_image", finalImageUrlParam);
            }
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
