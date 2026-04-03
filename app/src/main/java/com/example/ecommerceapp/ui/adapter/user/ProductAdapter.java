package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.Product; // Import đúng Model của bạn
import com.example.ecommerceapp.utils.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;

        // 1. Set Tên sản phẩm
        holder.tvProductName.setText(product.getName());

        // 2. Format Giá tiền từ BigDecimal sang chuỗi (VD: 150.000đ)
        if (product.getPrice() != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedPrice = formatter.format(product.getPrice()) + "đ";
            holder.tvProductPrice.setText(formattedPrice);
        }

        // 3. Xử lý phần "Đã bán" (Tạm thời ẩn đi vì Model chưa có)
        holder.tvSold.setVisibility(View.GONE);

        // 4. Load Hình ảnh từ đường link URL
        List<String> images = product.getImages();
        if (images != null && !images.isEmpty()) {
            String firstImageUrl = images.get(0);

            // Gọi class ImageLoader xịn xò của nhóm bạn:
            ImageLoader.load(holder.itemView.getContext(), holder.ivProductImage, firstImageUrl);

        } else {
            // Nếu sản phẩm không có link ảnh, set 1 cái ảnh mặc định
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }
}