package com.example.ecommerceapp.ui.adapter.seller.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.review.SellerReviewResponse;
import com.example.ecommerceapp.ui.viewholder.seller.review.SellerReviewVH;

import java.util.ArrayList;
import java.util.List;

public class SellerReviewAdapter extends RecyclerView.Adapter<SellerReviewVH> {

    public interface OnItemClickListener {
        void onClick(SellerReviewResponse item);
        void onReplyClick(SellerReviewResponse item, String replyContent);
        void onToggleClick(SellerReviewResponse item, int position);
    }

    private List<SellerReviewResponse> list = new ArrayList<>();
    private OnItemClickListener listener;

    private boolean isReplyVisible = false;
    private boolean isInputVisible = false;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setReplyVisible(boolean replyVisible) {
        isReplyVisible = replyVisible;
        notifyDataSetChanged();
    }

    public void setInputVisible(boolean inputVisible) {
        isInputVisible = inputVisible;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_review, parent, false);
        return new SellerReviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerReviewVH holder, int position) {
        SellerReviewResponse item = list.get(position);

        // Dùng bind trong VH
        holder.bind(item, isReplyVisible, isInputVisible);

        // Click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        // Click reply button
        holder.getBtnReply().setOnClickListener(v -> {
            if (listener != null) listener.onReplyClick(item, holder.getEtSellerReply().getText().toString());
        });

        // Click toggle show/hide
        holder.getTvToggle().setOnClickListener(v -> {
            if (listener != null) listener.onToggleClick(item, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<SellerReviewResponse> data) {
        list = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<SellerReviewResponse> data) {
        if (data != null && !data.isEmpty()) {
            int start = list.size();
            list.addAll(data);
            notifyItemRangeInserted(start, data.size());
        }
    }

    public SellerReviewResponse getItem(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }
}