package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.MessageResponse;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageResponse> list = new ArrayList<>();
    private int currentUserId;

    public MessageAdapter(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void updateData(List<MessageResponse> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public void addMessage(MessageResponse message) {
        this.list.add(message);
        notifyItemInserted(list.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageResponse message = list.get(position);

        // Kiểm tra xem ai là người gửi
        if (message.getSenderId() != null && message.getSenderId() == currentUserId) {
            // Là TÔI gửi -> Hiện bên phải màu cam, ẩn bên trái
            holder.layoutRight.setVisibility(View.VISIBLE);
            holder.layoutLeft.setVisibility(View.GONE);
            holder.tvRight.setText(message.getMessage());
        } else {
            // Là SHOP gửi -> Hiện bên trái màu xám, ẩn bên phải
            holder.layoutLeft.setVisibility(View.VISIBLE);
            holder.layoutRight.setVisibility(View.GONE);
            holder.tvLeft.setText(message.getMessage());

            if (message.getIsAiGenerated() != null && message.getIsAiGenerated()) {
                holder.imgAiIcon.setVisibility(View.VISIBLE);
            } else {
                holder.imgAiIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutLeft, layoutRight;
        TextView tvLeft, tvRight;
        android.widget.ImageView imgAiIcon;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutLeft = itemView.findViewById(R.id.layoutLeftMessage);
            layoutRight = itemView.findViewById(R.id.layoutRightMessage);
            tvLeft = itemView.findViewById(R.id.tvLeftMessage);
            tvRight = itemView.findViewById(R.id.tvRightMessage);
            imgAiIcon = itemView.findViewById(R.id.imgAiIcon);
        }
    }
}