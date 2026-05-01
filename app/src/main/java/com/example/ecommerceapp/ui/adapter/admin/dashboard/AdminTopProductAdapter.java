package com.example.ecommerceapp.ui.adapter.admin.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminTopProductResponse;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminTopProductAdapter extends RecyclerView.Adapter<AdminTopProductAdapter.ViewHolder> {

    public interface OnTopProductClickListener {
        void onClick(AdminTopProductResponse product);
    }

    private List<AdminTopProductResponse> productList = new ArrayList<>();
    private String currentType = "SOLD"; // "SOLD" or "REVENUE"
    private OnTopProductClickListener listener;

    public void setListener(OnTopProductClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<AdminTopProductResponse> productList, String type) {
        this.productList = productList;
        this.currentType = type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_top_product, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminTopProductResponse product = productList.get(position);
        
        int rank = position + 1;
        holder.tvRank.setText("#" + rank);
        
        int color;
        if (rank == 1) {
            color = android.graphics.Color.parseColor("#F5C542"); // Vàng (Gold)
        } else if (rank == 2) {
            color = android.graphics.Color.parseColor("#B0BEC5"); // Bạc (Silver)
        } else if (rank == 3) {
            color = android.graphics.Color.parseColor("#CD7F32"); // Đồng (Bronze)
        } else {
            color = android.graphics.Color.parseColor("#E0E0E0"); // Xám (Gray)
        }
        holder.tvRank.getBackground().setTint(color);

        holder.tvProductCode.setText(product.getProductCode() != null ? product.getProductCode() : "Mã: --");
        holder.tvProductName.setText(product.getName());
        holder.tvShopName.setText("Shop: " + product.getShopName());
        holder.tvPrice.setText(String.format("%,.0f", product.getPrice()) + " đ");

        ImageLoader.load(holder.itemView.getContext(), holder.ivProductImage, product.getImage());

        if ("REVENUE".equals(currentType)) {
            holder.tvSoldAndRevenue.setText("Doanh thu: " + NumberUtils.formatCompact(product.getRevenue()) + " ₫");
            holder.tvSoldAndRevenue.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else {
            holder.tvSoldAndRevenue.setText("Đã bán: " + NumberUtils.formatCompact(java.math.BigDecimal.valueOf(product.getSoldCount())));
            holder.tvSoldAndRevenue.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.purple));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvRank, tvProductCode, tvProductName, tvShopName, tvPrice, tvSoldAndRevenue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductCode = itemView.findViewById(R.id.tvProductCode);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSoldAndRevenue = itemView.findViewById(R.id.tvSoldAndRevenue);
        }
    }
}
