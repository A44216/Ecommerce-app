package com.example.ecommerceapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.assistant.AssistantChatItem;

import java.util.List;

public class AssistantChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<AssistantChatItem> chatItems;

    public AssistantChatAdapter(Context context, List<AssistantChatItem> chatItems) {
        this.context = context;
        this.chatItems = chatItems;
    }

    public void setChatItems(List<AssistantChatItem> chatItems) {
        this.chatItems = chatItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return chatItems.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AssistantChatItem.VIEW_TYPE_USER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_assistant_chat_user, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == AssistantChatItem.VIEW_TYPE_AI_CAROUSEL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_assistant_chat_ai_carousel, parent, false);
            return new AiCarouselViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_assistant_chat_ai_text, parent, false);
            return new AiTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AssistantChatItem item = chatItems.get(position);

        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvUserMessage.setText(item.getText());
        } else if (holder instanceof AiTextViewHolder) {
            ((AiTextViewHolder) holder).tvAiMessage.setText(item.getText());
        } else if (holder instanceof AiCarouselViewHolder) {
            AiCarouselViewHolder carouselHolder = (AiCarouselViewHolder) holder;
            carouselHolder.tvAiMessage.setText(item.getText());
            
            ProductCarouselAdapter productAdapter = new ProductCarouselAdapter(context, item.getProducts());
            carouselHolder.rvProductCarousel.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            carouselHolder.rvProductCarousel.setAdapter(productAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return chatItems != null ? chatItems.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
        }
    }

    public static class AiTextViewHolder extends RecyclerView.ViewHolder {
        TextView tvAiMessage;
        public AiTextViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
        }
    }

    public static class AiCarouselViewHolder extends RecyclerView.ViewHolder {
        TextView tvAiMessage;
        RecyclerView rvProductCarousel;
        public AiCarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
            rvProductCarousel = itemView.findViewById(R.id.rvProductCarousel);
        }
    }
}
