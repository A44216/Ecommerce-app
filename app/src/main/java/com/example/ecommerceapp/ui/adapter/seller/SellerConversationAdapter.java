package com.example.ecommerceapp.ui.adapter.seller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.ui.activity.home.user.chat.ChatActivity;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SellerConversationAdapter extends RecyclerView.Adapter<SellerConversationAdapter.ViewHolder> {

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

        holder.tvShopName.setText(conversation.getCustomerName());
        
        if (conversation.getCreatedAt() != null && conversation.getCreatedAt().length() >= 10) {
            String date = conversation.getCreatedAt().substring(0, 10);
            holder.tvDate.setText(date);
        } else {
            holder.tvDate.setText("");
        }

        ImageLoader.load(context, holder.ivShopAvatar, ""); 

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("CONVERSATION_ID", conversation.getId());
            intent.putExtra("SHOP_NAME", conversation.getCustomerName());
            intent.putExtra("IS_SELLER", true);
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
