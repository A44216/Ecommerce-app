package com.example.ecommerceapp.ui.viewholder.seller.review;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.seller.review.SellerReviewResponse;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.TimeUtils;

public class SellerReviewVH extends RecyclerView.ViewHolder {

    // USER INFO
    private ImageView imgUserAvatar;
    private TextView tvUserName, tvCreatedAt;
    private RatingBar ratingBar;

    // COMMENT
    private TextView tvComment;

    // SELLER REPLY
    private LinearLayout layoutSellerReply;
    private TextView tvSellerReply, tvSellerReplyAt;

    // SELLER INPUT
    private LinearLayout layoutSellerReplyInput;
    private EditText etSellerReply;
    private TextView btnReply;

    // TOGGLE SHOW/HIDE
    private TextView tvToggle;

    public SellerReviewVH(@NonNull View itemView) {
        super(itemView);

        imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
        tvUserName = itemView.findViewById(R.id.tvUserName);
        tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        ratingBar = itemView.findViewById(R.id.ratingBar);

        tvComment = itemView.findViewById(R.id.tvComment);

        layoutSellerReply = itemView.findViewById(R.id.layoutSellerReply);
        tvSellerReply = itemView.findViewById(R.id.tvSellerReply);
        tvSellerReplyAt = itemView.findViewById(R.id.tvSellerReplyAt);

        layoutSellerReplyInput = itemView.findViewById(R.id.layoutSellerReplyInput);
        etSellerReply = itemView.findViewById(R.id.etSellerReply);
        btnReply = itemView.findViewById(R.id.btnReply);

        tvToggle = itemView.findViewById(R.id.tvToggle);
    }

    // Bind dữ liệu
    public void bind(SellerReviewResponse item, boolean isInputVisible) {

        tvUserName.setText(item.getFullName());
        tvCreatedAt.setText(TimeUtils.formatDateTime(item.getCreatedAt()));
        ratingBar.setRating(item.getRating());
        tvComment.setText(item.getComment() != null ? item.getComment() : "");

        boolean isExpanded = isInputVisible;

        if (isExpanded) {
            tvComment.setMaxLines(Integer.MAX_VALUE);
            tvComment.setEllipsize(null);
        } else {
            tvComment.setMaxLines(1);
            tvComment.setEllipsize(android.text.TextUtils.TruncateAt.END);
        }

        ImageLoader.load(itemView.getContext(), imgUserAvatar, item.getUserAvatar());

        etSellerReply.setText("");

        if (item.getIsReplied() != null && item.getIsReplied()) {

            boolean hasReply = item.getIsReplied() != null && item.getIsReplied();

            // REPLY
            if (hasReply) {

                // chỉ hiện khi expand
                layoutSellerReply.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                layoutSellerReplyInput.setVisibility(View.GONE);

                tvSellerReply.setText(item.getSellerReply() != null ? item.getSellerReply() : "");
                tvSellerReplyAt.setText(TimeUtils.formatDateTime(item.getSellerReplyAt()));

            } else {

                // chưa reply → chỉ hiện input khi expand
                layoutSellerReply.setVisibility(View.GONE);
                layoutSellerReplyInput.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }

        } else {

            layoutSellerReply.setVisibility(View.GONE);
            layoutSellerReplyInput.setVisibility(
                    isInputVisible ? View.VISIBLE : View.GONE
            );
        }

        boolean hasReply = item.getIsReplied() != null && item.getIsReplied();

        tvToggle.setText(isExpanded ? "Ẩn" : (hasReply ? "Xem thêm" : "Phản hồi"));

    }

    // GETTERS & SETTERS
    public ImageView getImgUserAvatar() { return imgUserAvatar; }
    public void setImgUserAvatar(ImageView imgUserAvatar) { this.imgUserAvatar = imgUserAvatar; }

    public TextView getTvUserName() { return tvUserName; }
    public void setTvUserName(TextView tvUserName) { this.tvUserName = tvUserName; }

    public TextView getTvCreatedAt() { return tvCreatedAt; }
    public void setTvCreatedAt(TextView tvCreatedAt) { this.tvCreatedAt = tvCreatedAt; }

    public RatingBar getRatingBar() { return ratingBar; }
    public void setRatingBar(RatingBar ratingBar) { this.ratingBar = ratingBar; }

    public TextView getTvComment() { return tvComment; }
    public void setTvComment(TextView tvComment) { this.tvComment = tvComment; }

    public LinearLayout getLayoutSellerReply() { return layoutSellerReply; }
    public void setLayoutSellerReply(LinearLayout layoutSellerReply) { this.layoutSellerReply = layoutSellerReply; }

    public TextView getTvSellerReply() { return tvSellerReply; }
    public void setTvSellerReply(TextView tvSellerReply) { this.tvSellerReply = tvSellerReply; }

    public TextView getTvSellerReplyAt() { return tvSellerReplyAt; }
    public void setTvSellerReplyAt(TextView tvSellerReplyAt) { this.tvSellerReplyAt = tvSellerReplyAt; }

    public LinearLayout getLayoutSellerReplyInput() { return layoutSellerReplyInput; }
    public void setLayoutSellerReplyInput(LinearLayout layoutSellerReplyInput) { this.layoutSellerReplyInput = layoutSellerReplyInput; }

    public EditText getEtSellerReply() { return etSellerReply; }
    public void setEtSellerReply(EditText etSellerReply) { this.etSellerReply = etSellerReply; }

    public TextView getBtnReply() { return btnReply; }
    public void setBtnReply(TextView btnReply) { this.btnReply = btnReply; }

    public TextView getTvToggle() { return tvToggle; }
    public void setTvToggle(TextView tvToggle) { this.tvToggle = tvToggle; }
}