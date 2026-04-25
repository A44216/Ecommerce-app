package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import com.example.ecommerceapp.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class UserSuggestionAdapter extends RecyclerView.Adapter<UserSuggestionAdapter.ViewHolder> {
    private List<String> suggestions = new ArrayList<>();
    private final OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    public UserSuggestionAdapter(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<String> newData) {
        this.suggestions = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = suggestions.get(position);
        holder.tvSuggestionText.setText(item);
        holder.itemView.setOnClickListener(v -> listener.onSuggestionClick(item));
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSuggestionText;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSuggestionText = itemView.findViewById(R.id.tvSuggestionText);
        }
    }
}
