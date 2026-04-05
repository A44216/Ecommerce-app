package com.example.ecommerceapp.ui.adapter.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserProductImageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.activity.home.user.product.UserProductDetailActivity;
import com.example.ecommerceapp.utils.ImageLoader;

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
        this.productList = newProductList;
        notifyDataSetChanged(); // Báo cho RecyclerView vẽ lại giao diện
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
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice() != null ? product.getPrice().toString() : "0");
            if (finalImageUrl != null) {
                intent.putExtra("product_image", finalImageUrl);
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bạn nhớ check lại ID ở file item_product.xml xem có khớp không nhé
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}