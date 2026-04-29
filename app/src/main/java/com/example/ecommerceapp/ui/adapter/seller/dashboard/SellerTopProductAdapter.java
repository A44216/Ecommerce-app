package com.example.ecommerceapp.ui.adapter.seller.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerTopSellingProductResponse;
import com.example.ecommerceapp.ui.viewholder.seller.dashboard.SellerTopProductVH;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class SellerTopProductAdapter extends RecyclerView.Adapter<SellerTopProductVH> {

    public interface OnTopProductClickListener {
        void onClick(SellerTopSellingProductResponse product);
    }

    private OnTopProductClickListener listener;

    public static final int MODE_SOLD = 0;
    public static final int MODE_REVENUE = 1;

    private int displayMode = MODE_SOLD;

    private final List<SellerTopSellingProductResponse> list = new ArrayList<>();

    public void setListener(OnTopProductClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDisplayMode(int mode) {
        this.displayMode = mode;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SellerTopSellingProductResponse> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerTopProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_top_selling_product, parent, false);
        return new SellerTopProductVH(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull SellerTopProductVH holder, int position) {

        SellerTopSellingProductResponse item = list.get(position);

        int rank = position + 1;
        holder.getTvRank().setText("#" + rank);

        int color;
        if (rank == 1) {
            color = android.graphics.Color.parseColor("#F5C542");
        } else if (rank == 2) {
            color = android.graphics.Color.parseColor("#B0BEC5");
        } else if (rank == 3) {
            color = android.graphics.Color.parseColor("#CD7F32");
        } else {
            color = android.graphics.Color.parseColor("#E0E0E0");
        }

        holder.getTvRank().getBackground().setTint(color);

        holder.getTvProductCode().setText(item.getProductCode() != null ? item.getProductCode() : "Mã: --");
        holder.getTvName().setText(item.getName());

        holder.getTvPrice().setText(String.format("%,.0f", item.getPrice()) + " đ");

        if (displayMode == MODE_SOLD) {
            holder.getTvSoldAndRevenue().setText(
                    "Đã bán: " + item.getSoldQuantity()
            );
        } else {
            holder.getTvSoldAndRevenue().setText(
                    "Doanh thu: " + NumberUtils.formatCompact(item.getRevenue()) + " đ"
            );
        }

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.getImgProduct(),
                item.getImage()
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
