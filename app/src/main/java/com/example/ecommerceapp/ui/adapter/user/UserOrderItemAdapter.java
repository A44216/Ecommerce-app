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
            // Lưu ý: Nếu máy bạn không có file ic_image_placeholder, hãy đổi thành tên ảnh mặc định khác (ví dụ: ic_cart)
            holder.ivImage.setImageResource(R.drawable.ic_cart);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}