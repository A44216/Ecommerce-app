package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.SuggestionDisplayItem;
import com.example.ecommerceapp.utils.ImageLoader;

import java.text.DecimalFormat;

public class SuggestionListAdapter extends ListAdapter<SuggestionDisplayItem, SuggestionListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SuggestionDisplayItem item);
    }

    private final OnItemClickListener listener;

    public SuggestionListAdapter(OnItemClickListener listener) {
        super(new DiffUtil.ItemCallback<SuggestionDisplayItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull SuggestionDisplayItem oldItem, @NonNull SuggestionDisplayItem newItem) {
                return oldItem.getProduct().getProductId().equals(newItem.getProduct().getProductId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SuggestionDisplayItem oldItem, @NonNull SuggestionDisplayItem newItem) {
                return oldItem.getScore().equals(newItem.getScore()) &&
                        oldItem.getReason().equals(newItem.getReason());
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvScoreBadge, tvProductName, tvProductPrice, tvReason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvScoreBadge = itemView.findViewById(R.id.tvScoreBadge);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvReason = itemView.findViewById(R.id.tvReason);
        }

        public void bind(SuggestionDisplayItem item, OnItemClickListener listener) {
            tvProductName.setText(item.getProduct().getProductName());
            
            if (item.getProduct().getPrice() != null) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                tvProductPrice.setText(formatter.format(item.getProduct().getPrice()) + "đ");
            }
            
            tvReason.setText(item.getReason());
            
            // Score badge (percentage)
            int scorePercent = (int) (item.getScore().floatValue() * 100);
            tvScoreBadge.setText("Phù hợp " + scorePercent + "%");

            // Sử dụng getImageUrl() từ ProductBaseResponse
            if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().isEmpty()) {
                ImageLoader.load(itemView.getContext(), ivProductImage, item.getProduct().getImageUrl());
            } else {
                ivProductImage.setImageResource(R.drawable.img_placeholder);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
