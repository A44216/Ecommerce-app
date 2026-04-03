package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.Product;
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

        // Set Tên & Giá
        holder.tvProductName.setText(product.getName());
        if (product.getPrice() != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedPrice = formatter.format(product.getPrice()) + "đ";
            holder.tvProductPrice.setText(formattedPrice);
        }

        holder.tvSold.setVisibility(View.GONE);

        // Load Ảnh bằng ImageLoader của nhóm bạn
        List<String> images = product.getImages();
        if (images != null && !images.isEmpty()) {
            String firstImageUrl = images.get(0);
            ImageLoader.load(holder.itemView.getContext(), holder.ivProductImage, firstImageUrl);
        } else {
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // BẮT SỰ KIỆN CLICK VÀO SẢN PHẨM CHUYỂN SANG TRANG CHI TIẾT
        holder.itemView.setOnClickListener(v -> {

            // Khai báo đường dẫn chuẩn theo cấu trúc thư mục mới tạo
            android.content.Intent intent = new android.content.Intent(v.getContext(),
                    com.example.ecommerceapp.ui.activity.home.user.product.UserProductDetailActivity.class);

            // Truyền dữ liệu
            intent.putExtra("product_name", product.getName());
            if (product.getPrice() != null) {
                intent.putExtra("product_price", product.getPrice().toString());
            }
            if (images != null && !images.isEmpty()) {
                intent.putExtra("product_image", images.get(0));
            }

            v.getContext().startActivity(intent);
        });
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