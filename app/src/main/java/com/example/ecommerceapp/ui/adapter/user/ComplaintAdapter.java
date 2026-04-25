package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ComplaintResponse;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    private List<ComplaintResponse> list;

    public ComplaintAdapter(List<ComplaintResponse> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComplaintResponse item = list.get(position);
        holder.tvId.setText("Mã yêu cầu #" + item.getId());
        holder.tvContent.setText(item.getContent());
        holder.tvDate.setText(item.getCreatedAt());

        if ("PENDING".equals(item.getStatus())) {
            holder.tvStatus.setText("Đang xử lý");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        } else if ("RESOLVED".equals(item.getStatus())) {
            holder.tvStatus.setText("Đã giải quyết");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
        } else {
            holder.tvStatus.setText("Đã đóng");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvStatus, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvComplaintId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
