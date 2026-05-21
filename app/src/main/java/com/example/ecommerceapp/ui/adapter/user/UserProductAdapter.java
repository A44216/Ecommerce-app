package com.example.ecommerceapp.ui.adapter.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserProductImageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.activity.home.user.product.UserProductDetailActivity;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.NumberUtils;

import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.ProductViewHolder> {

    private Context context;
    private List<UserProductResponse> productList;

    public UserProductAdapter(Context context) {
        this.context = context;
        this.productList = new ArrayList<>(); // Khởi tạo list rỗng ban đầu
    }

    // Hàm này dùng để cập nhật lại dữ liệu khi gọi API thành công
    public void updateData(List<UserProductResponse> newProductList) {
        ProductDiffCallback diffCallback = new ProductDiffCallback(this.productList, newProductList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        
        this.productList.clear();
        this.productList.addAll(newProductList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        UserProductResponse product = productList.get(position);

        // 1. Gán Tên và Giá
        holder.tvProductName.setText(product.getName());
        if (product.getPrice() != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            holder.tvProductPrice.setText(formatter.format(product.getPrice()) + "đ");
        }

        int displaySoldCount = product.getSoldCount() != null ? product.getSoldCount() : 0;
        if (displaySoldCount >= 1000) {
            holder.tvSold.setText("Đã bán " + NumberUtils.formatCompact(BigDecimal.valueOf(displaySoldCount)).toLowerCase());
        } else {
            holder.tvSold.setText("Đã bán " + displaySoldCount);
        }

        // 2. Load Hình ảnh (Lấy ảnh đầu tiên trong mảng images trả về)
        String imageUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            UserProductImageResponse firstImage = product.getImages().get(0);
            if (firstImage != null && firstImage.getImageUrl() != null) {
                imageUrl = firstImage.getImageUrl();
                ImageLoader.load(context, holder.ivProductImage, imageUrl);
            }
        }
        if (imageUrl == null) {
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery); // Ảnh mặc định nếu lỗi
        }

        // 3. Sự kiện Click chuyển sang trang Chi tiết
        String finalImageUrl = imageUrl;
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProductDetailActivity.class);

            if (product.getId() != null) {
                intent.putExtra("product_id", product.getId());
            }

            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice() != null ? product.getPrice().toString() : "0");
            if (finalImageUrl != null) {
                intent.putExtra("product_image", finalImageUrl);
            }

            if (product.getShopId() != null) {
                intent.putExtra("shop_id", product.getShopId());
            }

            // ==========================================
            // TRUYỀN THÊM DỮ LIỆU ĐÁNH GIÁ, ĐÃ BÁN, KHO, MÔ TẢ
            // ==========================================
            // Xử lý an toàn tránh lỗi NullPointerException nếu dữ liệu từ DB trả về null
            float rating = product.getRatingAvg() != null ? product.getRatingAvg() : 0f;
            int soldCount = product.getSoldCount() != null ? product.getSoldCount() : 0;
            int stock = product.getStock() != null ? product.getStock() : 0;
            String desc = product.getDescription() != null ? product.getDescription() : "";

            intent.putExtra("product_rating_avg", rating);
            intent.putExtra("product_sold_count", soldCount);
            intent.putExtra("product_stock", stock);
            intent.putExtra("product_desc", desc);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvMatchScore, tvSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvMatchScore = itemView.findViewById(R.id.tvMatchScore);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }
}