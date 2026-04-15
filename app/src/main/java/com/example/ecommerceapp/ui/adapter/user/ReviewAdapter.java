package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ReviewResponse;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewResponse> list;

    public ReviewAdapter(List<ReviewResponse> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewResponse item = list.get(position);

        holder.tvName.setText(item.getUserName());
        holder.tvComment.setText(item.getComment());
        holder.ratingBar.setRating(item.getRating());

        if (item.getCreatedAt() != null) {
            // Cắt chuỗi lấy ngày nếu cần: 2026-04-10
            holder.tvDate.setText(item.getCreatedAt().substring(0, 10));
        }

        // Tạm thời ẩn phản hồi nếu Backend chưa trả về trường reply
        holder.layoutReply.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvComment, tvDate, tvReply;
        RatingBar ratingBar;
        LinearLayout layoutReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvReviewUserName);
            tvComment = itemView.findViewById(R.id.tvReviewComment);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
            tvReply = itemView.findViewById(R.id.tvSellerReply);
            ratingBar = itemView.findViewById(R.id.itemRatingBar);
            layoutReply = itemView.findViewById(R.id.layoutSellerReply);
        }
    }
}