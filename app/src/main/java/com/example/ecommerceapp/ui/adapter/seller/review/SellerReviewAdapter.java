package com.example.ecommerceapp.ui.adapter.seller.review;

import android.annotation.SuppressLint;
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

    private int expandedPosition = -1;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setReplyVisible(boolean replyVisible) {
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setInputVisible(boolean inputVisible) {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SellerReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_review, parent, false);
        return new SellerReviewVH(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull SellerReviewVH holder, @SuppressLint("RecyclerView") int position) {

        SellerReviewResponse item = list.get(position);

        boolean isInputVisible = (position == expandedPosition);
        holder.bind(item, isInputVisible);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        holder.getBtnReply().setOnClickListener(v -> {

            String reply = holder.getEtSellerReply()
                    .getText()
                    .toString()
                    .trim();

            if (reply.isEmpty()) {
                holder.getEtSellerReply().setError("Nhập nội dung phản hồi");
                return;
            }

            if (listener != null) {
                listener.onReplyClick(item, reply);
            }
        });

        holder.getTvToggle().setOnClickListener(v -> {

            if (expandedPosition == position) {
                expandedPosition = -1; // đóng
            } else {
                expandedPosition = position; // mở
            }

            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
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