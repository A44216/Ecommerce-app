package com.example.ecommerceapp.ui.adapter.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.ui.activity.home.user.chat.ChatActivity;
import com.example.ecommerceapp.utils.ImageLoader;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class UserConversationAdapter extends RecyclerView.Adapter<UserConversationAdapter.ViewHolder> {

    private List<ConversationResponse> list = new ArrayList<>();
    private Context context;

    public void setData(List<ConversationResponse> data) {
        this.list = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationResponse conversation = list.get(position);

        holder.tvShopName.setText(conversation.getShopName());
        
        if (conversation.getCreatedAt() != null && conversation.getCreatedAt().length() >= 10) {
            try {
                String dateStr = conversation.getCreatedAt();
                if (dateStr.length() > 19) {
                    dateStr = dateStr.substring(0, 19); // Bỏ phần milliseconds
                }
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                java.util.Date date = sdf.parse(dateStr);
                java.util.Calendar now = java.util.Calendar.getInstance();
                java.util.Calendar msgTime = java.util.Calendar.getInstance();
                msgTime.setTime(date);
                
                if (now.get(java.util.Calendar.YEAR) == msgTime.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) == msgTime.get(java.util.Calendar.DAY_OF_YEAR)) {
                    holder.tvDate.setText(new java.text.SimpleDateFormat("HH:mm").format(date));
                } else {
                    holder.tvDate.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(date));
                }
            } catch (Exception e) {
                holder.tvDate.setText(conversation.getCreatedAt().substring(0, 10));
            }
        } else {
            holder.tvDate.setText("");
        }

        ImageLoader.load(context, holder.ivShopAvatar, ""); // Backend chưa có avatar shop

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("CONVERSATION_ID", conversation.getId());
            intent.putExtra("SHOP_NAME", conversation.getShopName());
            intent.putExtra("SHOP_ID", conversation.getShopId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvShopName, tvDate;
        ImageView ivShopAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivShopAvatar = itemView.findViewById(R.id.ivShopAvatar);
        }
    }
}
