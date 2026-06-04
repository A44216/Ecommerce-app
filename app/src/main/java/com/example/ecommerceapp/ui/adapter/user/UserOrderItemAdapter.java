package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
// Đã sửa thành UserOrderItemResponse cho khớp với Model của bạn:
import com.example.ecommerceapp.data.model.response.UserOrderItemResponse;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.List;

public class UserOrderItemAdapter extends RecyclerView.Adapter<UserOrderItemAdapter.ViewHolder> {

    // Đã đổi kiểu List thành UserOrderItemResponse
    private List<UserOrderItemResponse> list;

    public UserOrderItemAdapter(List<UserOrderItemResponse> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserOrderItemResponse item = list.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvPrice.setText(String.format("%,.0fđ", item.getPrice()));

        // Load ảnh sản phẩm
        if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
            ImageLoader.load(holder.itemView.getContext(), holder.ivImage, item.getProductImage());
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_cart);
        }
        if (item.getIsReviewed() != null && item.getIsReviewed()) {
            holder.btnReview.setText("Xem đánh giá");
            // Sử dụng giao diện mặc định của nút (nền trắng viền xanh) từ item_order_product.xml
            holder.btnReview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.white, null)));
            holder.btnReview.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary_blue, null));

            holder.btnReview.setOnClickListener(v -> {
                android.content.Context context = holder.itemView.getContext();
                android.content.Intent intent = new android.content.Intent(context, com.example.ecommerceapp.ui.activity.home.user.review.ReviewActivity.class);
                intent.putExtra("PRODUCT_ID", item.getProductId());
                intent.putExtra("ORDER_ITEM_ID", item.getId());
                intent.putExtra("REVIEW_ID", item.getReviewId()); // Truyền reviewId
                intent.putExtra("MODE", "VIEW"); // Bật chế độ xem
                context.startActivity(intent);
            });
        } else {
            holder.btnReview.setText("Đánh giá");
            // Sử dụng giao diện mặc định của nút
            holder.btnReview.setBackgroundTintList(android.content.res.ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.white, null)));
            holder.btnReview.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary_blue, null));
            holder.btnReview.setOnClickListener(v -> {
                // Lấy Context (Môi trường hiện tại của nút bấm)
                android.content.Context context = holder.itemView.getContext();

                android.content.Intent intent = new android.content.Intent(context, com.example.ecommerceapp.ui.activity.home.user.review.ReviewActivity.class);
                intent.putExtra("PRODUCT_ID", item.getProductId()); // Gửi ID của sản phẩm này sang
                intent.putExtra("ORDER_ITEM_ID", item.getId()); // Gửi ID của OrderItem
                context.startActivity(intent); // Bắt đầu bay sang màn hình Đánh giá
            });
        }

        // Sự kiện khi nhấn vào cả item để xem chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = holder.itemView.getContext();
            android.content.Intent intent = new android.content.Intent(context, com.example.ecommerceapp.ui.activity.home.user.product.UserProductDetailActivity.class);
            
            // Gửi các thông tin cần thiết sang màn hình chi tiết
            intent.putExtra("product_id", item.getProductId());
            intent.putExtra("product_name", item.getProductName());
            intent.putExtra("product_price", item.getPrice() != null ? item.getPrice().toString() : "0");
            intent.putExtra("product_image", item.getProductImage());
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvQuantity, tvPrice;
        android.widget.Button btnReview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);

            btnReview = itemView.findViewById(R.id.btnReviewProduct);
        }
    }
}