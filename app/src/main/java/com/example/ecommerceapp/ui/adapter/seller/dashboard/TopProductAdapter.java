package com.example.ecommerceapp.ui.adapter.seller.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.dashboard.TopSellingProductResponse;
import com.example.ecommerceapp.ui.viewholder.seller.dashboard.TopProductVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductVH> {

    private List<TopSellingProductResponse> list = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<TopSellingProductResponse> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_selling_product, parent, false);
        return new TopProductVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TopProductVH holder, int position) {
        TopSellingProductResponse item = list.get(position);

        holder.getTvRank().setText("#" + String.valueOf(position + 1));
        holder.getTvName().setText(item.getName());
        holder.getTvPrice().setText(item.getRevenue() + " đ");
        holder.getTvSold().setText("Đã bán " + item.getSoldQuantity());

        ImageLoader.load(
                holder.itemView.getContext(),
                holder.getImgProduct(),
                item.getImage()
        );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
